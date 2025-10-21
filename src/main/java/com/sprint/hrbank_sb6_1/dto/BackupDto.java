package com.sprint.hrbank_sb6_1.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class BackupDto {
    private Long id;
    private String worker;
    private Instant startedAt;
    private Instant endedAt;
    private String status;
    private Long fileId;
}
