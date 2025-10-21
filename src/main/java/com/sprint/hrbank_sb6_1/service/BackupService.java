package com.sprint.hrbank_sb6_1.service;

import com.sprint.hrbank_sb6_1.dto.BackupDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BackupService {
    BackupDto CreateBackup();
    Page<BackupDto> GetAllBackups(Pageable pageable);
    BackupDto GetBackupByStatus(String status);
}
