package com.sprint.hrbank_sb6_1.backup.repository;

import com.sprint.hrbank_sb6_1.backup.dto.ScheduledBackupResponseDto;
import com.sprint.hrbank_sb6_1.backup.dto.ScheduledBackupSearchCondition;

import java.util.List;

public interface ScheduledBackupRepositoryCustom {
    List<ScheduledBackupResponseDto> search(ScheduledBackupSearchCondition condition);
}