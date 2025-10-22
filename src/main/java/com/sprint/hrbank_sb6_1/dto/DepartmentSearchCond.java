package com.sprint.hrbank_sb6_1.dto;

public record DepartmentSearchCond(
    String nameOrDescription,
    Integer idAfter,
    String cursor,                // 🔹 커서 필드 추가
        Integer size,
        DepartmentSortBy sortField,
        SortDirection sortDirection
) {
  public int safeSize() { return Math.max(size, 1); }
}