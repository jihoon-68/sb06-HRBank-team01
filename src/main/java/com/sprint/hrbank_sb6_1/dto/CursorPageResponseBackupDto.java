package com.sprint.hrbank_sb6_1.dto;
import java.util.List;
public record CursorPageResponseBackupDto(
        List<BackupDto> content,
        String nextCursor,
        Long nextIdAfter,
        int size,
        Long totalElements,
        boolean hasNext
) {}
