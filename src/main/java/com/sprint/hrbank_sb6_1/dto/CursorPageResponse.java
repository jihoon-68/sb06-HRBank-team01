package com.sprint.hrbank_sb6_1.dto;

import lombok.*;

import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageResponse<T> {
    private List<T> content;
    private String nextCursor;
    private Long nextIdAfter;
    private Integer size;
    private Long totalElements;
    private Boolean hasNext;

    public <U> CursorPageResponse<U> map(Function<T, U> converter) {
        List<U> convertContent = this.content.stream().map(converter).toList();
        return CursorPageResponse.<U>builder()
                .content(convertContent)
                .size(this.size)
                .totalElements(this.totalElements)
                .hasNext(this.hasNext)
                .nextCursor(this.nextCursor)
                .nextIdAfter(this.nextIdAfter)
                .build();
    }
}
