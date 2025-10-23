package com.sprint.hrbank_sb6_1.service.basic;

import com.sprint.hrbank_sb6_1.domain.Backup;
import com.sprint.hrbank_sb6_1.domain.BackupStatus;
import com.sprint.hrbank_sb6_1.domain.Employee;
import com.sprint.hrbank_sb6_1.domain.File;
import com.sprint.hrbank_sb6_1.event.BackupEvent;
import com.sprint.hrbank_sb6_1.repository.BackupRepository;
import com.sprint.hrbank_sb6_1.repository.EmployeeRepository;
import com.sprint.hrbank_sb6_1.repository.FileRepository;
import com.sprint.hrbank_sb6_1.service.BackupIoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@AllArgsConstructor
public class BasicBackupIoService implements BackupIoService {
    private final BackupRepository backupRepository;
    private final FileRepository fileRepository;
    private final EmployeeRepository employeeRepository;
    //추후에 환경설정으로 운영체재별 위지 값 들고 오는 걸로 변경
    private final String BACKUP_DIR = System.getProperty("os.name").toLowerCase().contains("win")? "C:/myWs/uploadedFiles" : "/Users/mac/myWs/uploadedFilesimg";
    private final String TIMESTAMP  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    @Async
    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void saveBackupData(BackupEvent event) {

        //현재 작업 중인 백업 객체 조회
        Backup backup = backupRepository.findById(event.getId())
                .orElseThrow(()-> new NoSuchElementException("Backup not found"));

        //파일 주소 설정
        String fileName ="employees_" + TIMESTAMP + ".csv";
        String filePath = BACKUP_DIR + fileName;
        Path path = Paths.get(filePath);

        log.info("비동기 CSV 백업 작업을 시작합니다. 저장 위치: {}", filePath);

        //백업 파일 객체 생상 설정
        File file =new File();
        file.setName(fileName);
        file.setType("application/json");
        AtomicInteger fileSize = new AtomicInteger();


        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
                //.csv헤더 설정
                writer.write("\uFEFF");
                writer.write("ID,사번,부서,직함,입사일,상태");
                writer.newLine();
                try (Stream<Employee> employeeStreamstream = employeeRepository.streamAll()) {
                    //.csv에 한줄식 입력 OOM 방지
                    employeeStreamstream.forEach(employee -> {
                        try {
                            String csvLine =convertToCsvLine(employee);
                            fileSize.addAndGet(csvLine.length());
                            writer.write(csvLine);
                            writer.newLine();
                        }catch (IOException e){
                            throw new RuntimeException("CSV 라인 쓰기 실패", e);
                        }
                    });
                }
            }
            log.info("비동기 CSV 백업 작업 성공. {}", filePath);

            file.setSize(fileSize.get());

            //파일정보 생성
            fileRepository.save(file);

            //백업 상태 성공으로 변경
            backup.setStatus(BackupStatus.COMPLETED);
            backup.setEndedAt(LocalDateTime.now());
            backup.setFile(file);
            backupRepository.save(backup);

            log.info("비동기 CSV 백업 파일 정보 저장 성공. {}", file);
        }catch(Exception e) {
            log.error("비동기 CSV 백업 작업, 백업 파일 정보 저장 중 심각한 오류 발생: {}", e.getMessage());
            //실패시 미완성 파일 삭제
            //백업 상태 실패로 변경
            try {
                Files.deleteIfExists(path);
                backup.setStatus(BackupStatus.FAILED);
                backup.setEndedAt(LocalDateTime.now());
                backupRepository.save(backup);
                log.info("불완전한 백업 파일을 삭제했습니다: {}", filePath);
            } catch (IOException ex) {
                log.error("불완전한 백업 파일 삭제에 실패했습니다: {}", filePath, ex);
            }
        }
    }

    //.csv 한줄 만들는 메소드
    private String convertToCsvLine(Employee employee) {
        return String.join(",",
                employee.getId().toString(),
                employee.getEmployeeNumber(),
                employee.getDepartment().getName(),
                employee.getPosition(),
                employee.getHireDate().toString(),
                employee.getStatus().toString());
    }
}
