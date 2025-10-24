package com.sprint.hrbank_sb6_1.backup.scheduled.repository;

import com.sprint.hrbank_sb6_1.backup.scheduled.dto.ScheduledBackupResponseDto;
import com.sprint.hrbank_sb6_1.backup.scheduled.dto.ScheduledBackupSearchCondition;

import java.util.List;

public interface ScheduledBackupRepositoryCustom {
    List<ScheduledBackupResponseDto> search(ScheduledBackupSearchCondition condition);
}