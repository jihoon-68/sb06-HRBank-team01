package com.sprint.hrbank_sb6_1.dto.request;

import com.sprint.hrbank_sb6_1.domain.BackupStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SearchBackupRequest {
    private String worker;
    private BackupStatus status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startedAtFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startedAtTo;

    private Long idAfter;
    private String cursor;

    //기본값 살정
    private Integer size = 30;
    private String sortField = "startedAt";
    private String sortDirection="DESC";
}