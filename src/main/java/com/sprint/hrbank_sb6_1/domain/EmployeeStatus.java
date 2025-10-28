package com.sprint.hrbank_sb6_1.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum EmployeeStatus {
    ACTIVE("재직중"),
    ON_LEAVE("휴직중"),
    RESIGNED("퇴사");

    private final String description;

    public static EmployeeStatus fromDescription(String description) {
        for (EmployeeStatus status : values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("잘못된 직원 상태 값입니다.");
    }
}