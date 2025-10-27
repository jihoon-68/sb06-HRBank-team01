package com.sprint.hrbank_sb6_1.dto;
import java.time.LocalDateTime;

public record BackupDto(
    Long id,
    String worker,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    String status,
    Long fileId
)
{}
