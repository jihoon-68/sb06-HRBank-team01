package com.sprint.hrbank_sb6_1.backup.service;

import com.sprint.hrbank_sb6_1.backup.domain.ScheduledBackup;
import com.sprint.hrbank_sb6_1.backup.domain.ScheduledBackupStatus;
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

        // 백업 엔티티 생성
        ScheduledBackup backup = new ScheduledBackup();
        backup.setWorker("system");
        backup.setStartedAt(LocalDateTime.now());
        backup.setStatus(ScheduledBackupStatus.진행중);
        scheduledBackupRepository.save(backup);

        try {
            // 직원 데이터 조회
            List<Employee> employees = employeeRepository.findAll();

            // CSV 파일 생성
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

            // File 엔티티 저장
            File backupFile = new File();
            backupFile.setName(filename);
            backupFile.setType("CSV");
            backupFile.setSize((int) new java.io.File(fullPath).length());
            backupFile.setBackupTask(backup);

            fileRepository.save(backupFile);
            backup.addFile(backupFile);

            // 상태 완료
            backup.setStatus(ScheduledBackupStatus.완료);
            backup.setEndedAt(LocalDateTime.now());
            scheduledBackupRepository.save(backup);

            log.info("✅ [자동 백업 완료] 파일명: {}", filename);

        } catch (Exception e) {
            // 실패 처리
            backup.setStatus(ScheduledBackupStatus.실패);
            backup.setEndedAt(LocalDateTime.now());
            scheduledBackupRepository.save(backup);

            log.error("❌ [자동 백업 실패] {}", e.getMessage());
        }
    }
}