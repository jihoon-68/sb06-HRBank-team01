package com.sprint.hrbank_sb6_1.backup.repository;

import com.sprint.hrbank_sb6_1.domain.BackupTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupTaskRepository extends JpaRepository<BackupTask, Long> {
}