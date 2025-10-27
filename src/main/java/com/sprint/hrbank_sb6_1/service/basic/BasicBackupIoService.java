package com.sprint.hrbank_sb6_1.service.basic;

import com.sprint.hrbank_sb6_1.domain.Backup;
import com.sprint.hrbank_sb6_1.domain.Employee;
import com.sprint.hrbank_sb6_1.event.BackupEvent;
import com.sprint.hrbank_sb6_1.event.BackupIoEvent;
import com.sprint.hrbank_sb6_1.repository.BackupRepository;
import com.sprint.hrbank_sb6_1.repository.EmployeeRepository;
import com.sprint.hrbank_sb6_1.service.BackupIoService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@Transactional
public class BasicBackupIoService implements BackupIoService {
    private final BackupRepository backupRepository;
    private final EmployeeRepository employeeRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Path rootPath;

    public BasicBackupIoService(BackupRepository backupRepository,
                                EmployeeRepository employeeRepository,
                                ApplicationEventPublisher eventPublisher,
                                @Value("${spring.hrbank.storage.local.root-path}") String path
    ) {
        this.backupRepository = backupRepository;
        this.employeeRepository = employeeRepository;
        this.eventPublisher = eventPublisher;
        this.rootPath = Paths.get(System.getProperty("user.dir"), path);
    }

    private final String TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    @Async
    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void saveBackupData(BackupEvent event) {

        //현재 작업 중인 백업 객체 조회
        Backup backup = backupRepository.findById(event.id())
                .orElseThrow(() -> new NoSuchElementException("Backup not found"));

        //파일 주소 설정
        String fileName = "employees_" + TIMESTAMP + ".csv";
        Path path = Paths.get(String.valueOf(rootPath), fileName);

        log.info("비동기 CSV 백업 작업을 시작합니다. 저장 위치: {}", path);

        try {
            Files.createDirectories(rootPath);
            try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
                //.csv헤더 설정
                writer.write("\uFEFF");
                writer.write("ID,사번,부서,직함,입사일,상태");
                writer.newLine();
                try (Stream<Employee> employeeStreamstream = employeeRepository.streamAll()) {
                    //.csv에 한줄식 입력 OOM 방지
                    employeeStreamstream.forEach(employee -> {
                        try {
                            String csvLine = convertToCsvLine(employee);
                            writer.write(csvLine);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new RuntimeException("CSV 라인 쓰기 실패", e);
                        }
                    });
                }
            }
            log.info("비동기 CSV 백업 작업 성공. {}", path);

            //백업상태,파일정보 저장 이벤트 등록
            eventPublisher.publishEvent(new BackupIoEvent(backup,fileName,"text/csv",(int) Files.size(path),false));

            log.info("비동기 CSV 백업 파일 정보 저장 성공. {}", fileName);
        } catch (Exception e) {
            log.error("비동기 CSV 백업 작업, 백업 파일 정보 저장 중 심각한 오류 발생: {}", e.getMessage());
            try {
                //실패시 미완성 파일 삭제
                Files.deleteIfExists(path);

                //백업 실패 로그 파일 작성
                String logFileName = TIMESTAMP+".log";
                Path logPath = Paths.get(String.valueOf(rootPath), logFileName);
                Files.writeString(logPath, e.toString(), UTF_8);

                //백업상태,파일정보 저장 이벤트 등록
                eventPublisher.publishEvent(new BackupIoEvent(backup,logFileName,"text/plain",(int) Files.size(path),true));

                log.info("불완전한 백업 파일을 삭제했습니다: {}", fileName);

            } catch (IOException ex) {
                log.error("불완전한 백업 파일 삭제와 로그생성에 실패했습니다: {}", fileName, ex);
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
