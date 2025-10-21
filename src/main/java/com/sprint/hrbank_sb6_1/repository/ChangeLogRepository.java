package com.sprint.hrbank_sb6_1.repository;

import com.sprint.hrbank_sb6_1.domain.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {
}
