package com.sprint.hrbank_sb6_1.backup.scheduled.repository;

import com.sprint.hrbank_sb6_1.backup.scheduled.domain.ScheduledBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledBackupRepository extends JpaRepository<ScheduledBackup, Long> {
}