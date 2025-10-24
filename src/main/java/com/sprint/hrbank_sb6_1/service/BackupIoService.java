package com.sprint.hrbank_sb6_1.service;

import com.sprint.hrbank_sb6_1.event.BackupEvent;

public interface BackupIoService {
    void saveBackupData(BackupEvent event);
}