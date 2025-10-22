package com.sprint.hrbank_sb6_1.dto;

import com.sprint.hrbank_sb6_1.domain.BackupStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class SearchBackupRequest {
    private String worker;
    private BackupStatus status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startedAtFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startedAtTo;

    private Long idAfter;
    private String cursor;

    private Integer size;
    private String sortField;
    private String sortDirection;


}
