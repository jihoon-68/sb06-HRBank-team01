package com.sprint.hrbank_sb6_1.dto;

public record DepartmentSearchCond(
    String nameOrDescription,
    Long idAfter,
    String cursor,                // 🔹 커서 필드 추가
    Integer size,
    DepartmentSortBy sortField,
    SortDirection sortDirection
) {
}