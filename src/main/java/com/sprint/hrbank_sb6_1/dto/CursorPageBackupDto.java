package com.sprint.hrbank_sb6_1.dto;

public record CursorPageBackupDto(
        String nextCursor,
        Long nextIdAfter,
        Long totalElements
) {

}
