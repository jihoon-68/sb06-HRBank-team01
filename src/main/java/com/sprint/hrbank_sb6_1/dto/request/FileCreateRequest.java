package com.sprint.hrbank_sb6_1.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileCreateRequest {
    private String name;
    private String type;
    private int size;
    private byte[] bytes;
}
