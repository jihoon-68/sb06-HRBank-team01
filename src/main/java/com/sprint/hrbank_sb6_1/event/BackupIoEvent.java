package com.sprint.hrbank_sb6_1.event;

import com.sprint.hrbank_sb6_1.domain.Backup;

public record BackupIoEvent(
        Backup backup,
        String fileName,
        String type,
        Integer size,
        boolean err
) {
}
