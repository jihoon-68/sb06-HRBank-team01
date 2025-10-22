package com.sprint.hrbank_sb6_1.dto;

import java.util.List;

public record CursorPagedResponse<T>(
    List<T> content,
    String nextCursor,     // 또는 null
    Long nextIdAfter,   // 정수 id를 커서로 쓰면 이 필드 사용
    int size,
    long totalElements,    // 필요 없으면 제거 가능(커서 방식은 보통 안 넣음)
    boolean hasNext
) {
  public static <T> CursorPagedResponse<T> of(
      List<T> content, String nextCursor, Long nextIdAfter,
      int size, long totalElements, boolean hasNext
  ) {
    return new CursorPagedResponse<>(content, nextCursor, nextIdAfter, size, totalElements, hasNext);
  }
}