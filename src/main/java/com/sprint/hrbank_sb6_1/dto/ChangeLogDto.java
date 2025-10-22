package com.sprint.hrbank_sb6_1.dto;

import com.sprint.hrbank_sb6_1.domain.ChangeLog;
import java.time.LocalDateTime;

public record ChangeLogDto(
    Long id,
    String type,
    String employeeNumber,
    String memo,
    String ipAddress,
    LocalDateTime at
) {
    public static ChangeLogDto from(ChangeLog changeLog) {
        return new ChangeLogDto(
            changeLog.getId(),
            changeLog.getStatus().name(),
            changeLog.getEmployee().getEmployeeNumber(),
            changeLog.getMemo(),
            changeLog.getAddress(),
            changeLog.getAt()
        );
    }
}
