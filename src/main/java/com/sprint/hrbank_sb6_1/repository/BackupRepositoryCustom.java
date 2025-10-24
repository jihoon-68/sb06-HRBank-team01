package com.sprint.hrbank_sb6_1.repository;
import com.sprint.hrbank_sb6_1.domain.Backup;
import com.sprint.hrbank_sb6_1.dto.SearchBackupRequest;

import org.springframework.data.domain.Slice;

public interface BackupRepositoryCustom {
    Slice<Backup> searchTasks(SearchBackupRequest searchBackupRequest);
    long countTasks(SearchBackupRequest search);

}