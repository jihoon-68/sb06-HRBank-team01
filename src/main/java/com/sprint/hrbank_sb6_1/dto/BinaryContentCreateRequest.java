package com.sprint.hrbank_sb6_1.dto;

import lombok.Builder;

@Builder
public record BinaryContentCreateRequest(
    String fileName,
    String contentType,
    byte[] bytes
) {


}
