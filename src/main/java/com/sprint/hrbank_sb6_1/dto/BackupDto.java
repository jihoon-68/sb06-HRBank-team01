package com.sprint.hrbank_sb6_1.dto;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class BackupDto {
    private Long id;
    private String worker;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String status;
    private Long fileId;
}
