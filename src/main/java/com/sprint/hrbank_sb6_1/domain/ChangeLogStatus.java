package com.sprint.hrbank_sb6_1.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChangeLogStatus {
    CREATED(1, "CREATED"),
    UPDATED(2, "UPDATED"),
    DELETED(3, "DELETED");

    private final int code;
    private final String description;

    public static ChangeLogStatus fromCode(int code) {
        for (ChangeLogStatus status : ChangeLogStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ChangeLogStatus code: " + code);
    }

    public static ChangeLogStatus fromDescription(String description) {
        for(ChangeLogStatus status : ChangeLogStatus.values()) {
            if(status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ChangeLogStatus description: " + description);
    }
}