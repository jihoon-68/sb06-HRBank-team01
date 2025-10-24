package com.sprint.hrbank_sb6_1.service;

import com.sprint.hrbank_sb6_1.domain.BackupStatus;
import com.sprint.hrbank_sb6_1.dto.BackupDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponseBackupDto;
import com.sprint.hrbank_sb6_1.dto.SearchBackupRequest;

public interface BackupService {
    BackupDto CreateBackup(String ip);
    CursorPageResponseBackupDto GetAllBackups(SearchBackupRequest searchBackupRequest);
    BackupDto GetBackupByStatus(BackupStatus status);
}