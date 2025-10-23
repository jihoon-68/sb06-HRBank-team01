package com.sprint.hrbank_sb6_1.backup.service;

import com.sprint.hrbank_sb6_1.backup.repository.BackupTaskRepository;
import com.sprint.hrbank_sb6_1.repository.FileRepository;
import com.sprint.hrbank_sb6_1.domain.BackupTask;
import com.sprint.hrbank_sb6_1.domain.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private final BackupTaskRepository backupTaskRepository;
    private final FileRepository fileRepository;


    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void runScheduledBackup() {
        log.info("자동 백업 프로세스 시작");

        // 백업 작업 생성
        BackupTask backupTask = new BackupTask();
        backupTask.setName("Daily Backup " + LocalDateTime.now());
        backupTask.setWorker("system");
        backupTask.setStatus("진행중");
        backupTask.setCreatedAt(LocalDateTime.now());

        backupTaskRepository.save(backupTask);

        try {
            // 백업 파일 생성
            File backupFile = new File();
            backupFile.setName("backup_" + LocalDateTime.now());
            backupFile.setType("AUTO_BACKUP");
            backupFile.setSize(1024); // 예시: KB
            backupFile.setBackupTask(backupTask);

            fileRepository.save(backupFile);

            // BackupTask에 파일 추가
            backupTask.getFiles().add(backupFile);

            // 백업 완료
            backupTask.setStatus("완료");
            backupTask.setCompletedAt(LocalDateTime.now());

            backupTaskRepository.save(backupTask);

            log.info("자동 백업 완료: {}", backupFile.getName());
        } catch (Exception e) {
            log.error("자동 백업 실패: {}", e.getMessage());

            // 실패 시 상태 업데이트
            backupTask.setStatus("실패");
            backupTask.setCompletedAt(LocalDateTime.now());
            backupTaskRepository.save(backupTask);
        }
    }
}