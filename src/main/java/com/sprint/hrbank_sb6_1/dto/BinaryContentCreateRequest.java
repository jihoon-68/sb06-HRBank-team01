package com.sprint.hrbank_sb6_1.dto;

public record BinaryContentCreateRequest(
    String fileName,
    String contentType,
    byte[] bytes
) {


}
