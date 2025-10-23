package com.sprint.hrbank_sb6_1.domain;

import lombok.Getter;

@Getter
public enum BackupStatus {
    IN_PROGRESS("진행중"),
    COMPLETED("진행완료"),
    FAILED("실패"),
    SKIPPED("건너뜀");

    private final String title;

    BackupStatus(String title) {
        this.title = title;
    }

}
