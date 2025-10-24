package com.sprint.hrbank_sb6_1.backup.dto;

import com.sprint.hrbank_sb6_1.backup.domain.ScheduledBackupStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ScheduledBackupResponseDto {
    private Long id;
    private String worker;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private ScheduledBackupStatus status;
    private Integer fileCount;
}