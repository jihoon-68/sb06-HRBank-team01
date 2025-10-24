package com.sprint.hrbank_sb6_1.backup.dto;

import com.sprint.hrbank_sb6_1.backup.domain.ScheduledBackupStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduledBackupSearchCondition {

    private String worker;                      // LIKE 검색
    private LocalDateTime startedAtFrom;        // 시작일 ~
    private LocalDateTime startedAtTo;          // ~ 시작일
    private ScheduledBackupStatus status;       // EXACT (Enum)

    private Long lastId;                        // 커서(이전 페이지 마지막 ID)
    private Integer size;                       // 페이지 크기 (기본 10)

    // 정렬: swagger 컨벤션에 맞춰 필드명 확정
    private String sortField;                   // startedAt | endedAt | status (기본 startedAt)
    private String sortDirection;               // ASC | DESC (기본 DESC)
}