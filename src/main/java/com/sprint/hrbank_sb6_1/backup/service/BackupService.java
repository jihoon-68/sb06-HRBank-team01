package com.sprint.hrbank_sb6_1.backup.service;

import com.sprint.hrbank_sb6_1.backup.domain.Backup;
import com.sprint.hrbank_sb6_1.backup.domain.BackupStatus;
import com.sprint.hrbank_sb6_1.backup.repository.BackupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BackupService {

    private final BackupRepository backupRepository;

    @Transactional
    public void performBackup() {
        Backup backup = new Backup();
        backup.setWorker("system");
        backup.setStartedAt(LocalDateTime.now());
        backup.setStatus(BackupStatus.진행중);
        backupRepository.save(backup);

        try {

            backup.setStatus(BackupStatus.완료);
        } catch (Exception e) {
            backup.setStatus(BackupStatus.실패);

        } finally {
            backup.setEndedAt(LocalDateTime.now());
            backupRepository.save(backup);
        }
    }
}