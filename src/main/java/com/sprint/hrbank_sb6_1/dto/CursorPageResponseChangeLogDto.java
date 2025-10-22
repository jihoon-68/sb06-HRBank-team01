package com.sprint.hrbank_sb6_1.dto;

import com.sprint.hrbank_sb6_1.domain.ChangeLog;
import java.util.List;

public record CursorPageResponseChangeLogDto(
    List<ChangeLogDto> content,
    String nextCursor,
    int nextIdAfter,
    int size,
    int totalElements,
    boolean hasNext
) {


}
