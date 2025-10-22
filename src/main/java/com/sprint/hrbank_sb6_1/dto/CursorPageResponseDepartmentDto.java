package com.sprint.hrbank_sb6_1.dto;

import lombok.Data;

import java.util.List;

@Data
public class CursorPageResponseDepartmentDto<T> {
    List<T> content;
    String nextCursor;
    String nextIdAfter;
    int size;
    Long totalElements;
    boolean hasNext;
}
