package com.sprint.hrbank_sb6_1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CursorPageBackupDto {
    String nextCursor;
    Long nextIdAfter;
    Long totalElements;
}
