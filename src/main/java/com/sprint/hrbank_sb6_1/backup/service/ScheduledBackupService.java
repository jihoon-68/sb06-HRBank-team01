package com.sprint.hrbank_sb6_1.backup.service;

import com.sprint.hrbank_sb6_1.backup.domain.ScheduledBackup;
import com.sprint.hrbank_sb6_1.backup.repository.ScheduledBackupRepository;
import com.sprint.hrbank_sb6_1.domain.Employee;
import com.sprint.hrbank_sb6_1.domain.File;
import com.sprint.hrbank_sb6_1.repository.EmployeeRepository;
import com.sprint.hrbank_sb6_1.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledBackupService {

    private final ScheduledBackupRepository scheduledBackupRepository;
    private final EmployeeRepository employeeRepository;
    private final FileRepository fileRepository;

    @Transactional
    public void performScheduledBackup() {
        log.info(" 🔄 [자동 백업 시작]");

        // 1) 백업 이력 생성 (시작)
        ScheduledBackup backup = new ScheduledBackup();
        backup.setWorker("system");
        backup.setStartedAt(LocalDateTime.now());
        backup.setStatus("IN_PROGRESS");
        scheduledBackupRepository.save(backup);

        try {
            // 2) 직원 데이터 조회
            List<Employee> employees = employeeRepository.findAll();

            // 3) CSV 파일 생성
            String filename = "employee_backup_" + System.currentTimeMillis() + ".csv";
            String directoryPath = "src/main/resources/backup/";
            String fullPath = directoryPath + filename;

            java.io.File dir = new java.io.File(directoryPath);
            if (!dir.exists()) dir.mkdirs();

            try (FileWriter writer = new FileWriter(fullPath)) {
                writer.write("id,name,number,title,hiredate,status\n");
                for (Employee e : employees) {
                    writer.write(String.format("%d,%s,%s,%s,%s,%s\n",
                            e.getId(),
                            e.getName(),
                            e.getEmployeeNumber(),
                            e.getPosition(),
                            e.getHireDate(),
                            e.getStatus()
                    ));
                }
            }

            // 4) File 엔티티 저장 (CSV)
            File backupFile = new File();
            backupFile.setName(filename);
            backupFile.setType("CSV");
            backupFile.setSize((int) new java.io.File(fullPath).length());
            fileRepository.save(backupFile);

            // 5) 백업 엔티티에 파일 연결 + 상태 갱신
            backup.setBackupFile(backupFile);
            backup.setStatus("COMPLETE");
            backup.setEndedAt(LocalDateTime.now());
            scheduledBackupRepository.save(backup);

            log.info("✅ [자동 백업 완료] 파일명: {}", filename);

        } catch (Exception ex) {
            log.error("❌ [자동 백업 실패] {}", ex.getMessage());

            try {
                // 6) 실패 로그 파일 생성
                String logFilename = "error_backup_" + System.currentTimeMillis() + ".log";
                String directoryPath = "src/main/resources/backup/";
                String fullPath = directoryPath + logFilename;

                try (FileWriter logWriter = new FileWriter(fullPath)) {
                    logWriter.write("[백업 실패 로그]\n");
                    logWriter.write("시간: " + LocalDateTime.now() + "\n");
                    logWriter.write("오류: " + ex.toString() + "\n");
                }

                // 7) File 엔티티 저장 (LOG)
                File logFile = new File();
                logFile.setName(logFilename);
                logFile.setType("LOG");
                logFile.setSize((int) new java.io.File(fullPath).length());
                fileRepository.save(logFile);

                // 8) 백업 엔티티에 로그 파일 연결
                backup.setBackupFile(logFile);

            } catch (Exception logEx) {
                log.error("❌ [로그 파일 생성 실패] {}", logEx.getMessage());
            }

            // 9) 상태 갱신
            backup.setStatus("FAILED");
            backup.setEndedAt(LocalDateTime.now());
            scheduledBackupRepository.save(backup);
        }
    }
}