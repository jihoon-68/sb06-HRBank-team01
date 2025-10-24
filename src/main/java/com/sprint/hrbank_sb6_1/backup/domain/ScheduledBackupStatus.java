package com.sprint.hrbank_sb6_1.backup.domain;

public enum ScheduledBackupStatus {
    IN_PROGRESS,   // 진행중
    COMPLETED,     // 완료
    FAILED,        // 실패
    SKIPPED        // 건너뜀
}
