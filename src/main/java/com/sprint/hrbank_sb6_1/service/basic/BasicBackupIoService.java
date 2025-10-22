package com.sprint.hrbank_sb6_1.service.basic;

import com.sprint.hrbank_sb6_1.repository.BackupRepository;
import com.sprint.hrbank_sb6_1.service.BackupIoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BasicBackupIoService implements BackupIoService {
    private final BackupRepository backupRepository;

    @Override
    public void saveBackupData() {

    }
}
