package com.sprint.hrbank_sb6_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BackupRepository extends JpaRepository<Backup, Long> , BackupRepositoryCustom {
    Optional<Backup> findTopByStatusOrderByStartedAtDesc(BackupStatus status);
}
