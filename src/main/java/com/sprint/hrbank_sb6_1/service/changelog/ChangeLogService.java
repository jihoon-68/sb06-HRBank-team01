package com.sprint.hrbank_sb6_1.service.changelog;

import com.sprint.hrbank_sb6_1.dto.CursorPageResponseChangeLogDto;
import com.sprint.hrbank_sb6_1.dto.DiffDto;
import java.time.LocalDateTime;
import java.util.List;

public interface ChangeLogService {

    CursorPageResponseChangeLogDto getChangeLog(
        String employeeNumber,
        String type,
        String memo,
        String ipAddress,
        LocalDateTime atFrom,
        LocalDateTime atTo,
        Long idAfter,
        int size,
        String sortField,
        String sortDirection
    );

    List<DiffDto> getChangeLogDiffs(Long changeLogId);

    Long countChangeLogs(LocalDateTime fromDate, LocalDateTime toDate);
}
