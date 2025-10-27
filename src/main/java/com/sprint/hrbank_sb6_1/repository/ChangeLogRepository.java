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
public interface ChangeLogRepository extends JpaRepository<ChangeLog,Long>, ChangeLogRepositoryCustom{
}
