package com.sprint.hrbank_sb6_1.dto;

import com.sprint.hrbank_sb6_1.domain.ChangeLog;
import com.sprint.hrbank_sb6_1.domain.ChangeLogStatus;
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
            ChangeLogStatus.fromCode(changeLog.getStatus()).getDescription(),
            changeLog.getEmployee() != null ? changeLog.getEmployee().getEmployeeNumber() : null,
            changeLog.getMemo(),
            changeLog.getAddress(),
            changeLog.getAt()
        );
    }
}
