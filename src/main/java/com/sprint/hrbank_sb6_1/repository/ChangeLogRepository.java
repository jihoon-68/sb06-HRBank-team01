package com.sprint.hrbank_sb6_1.repository;

import com.sprint.hrbank_sb6_1.domain.ChangeLog;
import com.sprint.hrbank_sb6_1.domain.ChangeLogStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog,Long> {


    @Query("""
        SELECT c FROM ChangeLog c
        WHERE (:employeeNumber IS NULL OR c.employee.employeeNumber LIKE %:employeeNumber%)
          AND (:type IS NULL OR c.status = :type)
          AND (:memo IS NULL OR c.memo LIKE %:memo%)
          AND (:ipAddress IS NULL OR c.address LIKE %:ipAddress%)
          AND (:atFrom IS NULL OR c.at >= :atFrom)
          AND (:atTo IS NULL OR c.at <= :atTo)
          AND (:idAfter IS NULL OR c.id > :idAfter)
        """)
    List<ChangeLog> searchChangeLogs(
        @Param("employeeNumber") String employeeNumber,
        @Param("type") ChangeLogStatus type,
        @Param("memo") String memo,
        @Param("ipAddress") String ipAddress,
        @Param("atFrom") LocalDateTime atFrom,
        @Param("atTo") LocalDateTime atTo,
        @Param("idAfter") Long idAfter,
        Pageable pageable
    );

    @Query("""
        SELECT COUNT(c) FROM ChangeLog c
        WHERE (:atFrom IS NULL OR c.at >= :atFrom)
          AND (:atTo IS NULL OR c.at <= :atTo)
        """)
    Long countByAtBetween(
        @Param("atFrom") LocalDateTime atFrom,
        @Param("atTo") LocalDateTime atTo
    );
}
