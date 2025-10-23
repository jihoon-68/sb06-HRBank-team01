package com.sprint.hrbank_sb6_1.dto;

import lombok.Getter;

public record DiffDto(
    String propertyName,
    String before,
    String after
) {

}
