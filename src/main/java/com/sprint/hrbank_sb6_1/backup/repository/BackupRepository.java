package com.sprint.hrbank_sb6_1.backup.repository;

import com.sprint.hrbank_sb6_1.backup.domain.ScheduledBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupRepository extends JpaRepository<ScheduledBackup, Long> {

}