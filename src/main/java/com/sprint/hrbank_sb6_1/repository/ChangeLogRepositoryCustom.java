package com.sprint.hrbank_sb6_1.repository;

import com.sprint.hrbank_sb6_1.domain.ChangeLog;
import com.sprint.hrbank_sb6_1.domain.ChangeLogStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ChangeLogRepositoryCustom {
    List<ChangeLog> searchChangeLogs(
        String employeeNumber,
        ChangeLogStatus type,
        String memo,
        String ipAddress,
        LocalDateTime atFrom,
        LocalDateTime atTo,
        Long idAfter,
        Pageable pageable
    );

    Long countByAtBetween(LocalDateTime fromDate, LocalDateTime toDate);
}
