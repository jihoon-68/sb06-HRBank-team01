package com.sprint.hrbank_sb6_1.dto;

import com.sprint.hrbank_sb6_1.domain.ChangeDiff;
import lombok.Getter;

public record DiffDto(
    String propertyName,
    String before,
    String after
) {

    public static DiffDto from(ChangeDiff changeDiff) {
        return new DiffDto(
            changeDiff.getPropertyName(),
            changeDiff.getOldValue(),
            changeDiff.getNewValue()
        );
    }
}
