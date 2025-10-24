package com.sprint.hrbank_sb6_1.backup.dto;

import com.sprint.hrbank_sb6_1.backup.domain.ScheduledBackupStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduledBackupSearchCondition {

    private String worker;               // LIKE 검색
    private LocalDateTime startedAtFrom; // 시작일 범위 (from)
    private LocalDateTime startedAtTo;   // 시작일 범위 (to)
    private ScheduledBackupStatus status; // EXACT 검색
    private Long lastId;                 // 커서 기반 페이징
    private Integer size;                // 페이지 사이즈
    private String orderBy;              // "startedAt" | "endedAt"
}