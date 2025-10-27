package com.sprint.hrbank_sb6_1.domain;

import com.sprint.hrbank_sb6_1.backup.scheduled.domain.ScheduledBackupStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Backup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String worker;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BackupStatus status;

    @OneToOne
    private File file;

    // 상태 업데이트
    public void markInProgress(String worker) {
        this.worker = worker;
        this.status = BackupStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void markCompleted(File csvFile) {
        this.status = BackupStatus.COMPLETED;
        this.endedAt = LocalDateTime.now();
        this.file = csvFile;
    }

    public void markFailed(File logFile) {
        this.status = BackupStatus.FAILED;
        this.endedAt = LocalDateTime.now();
        this.file = logFile;
    }

    public void markSkipped(String worker) {
        this.worker = worker;
        this.status = BackupStatus.SKIPPED;
        this.endedAt = LocalDateTime.now();
    }


}