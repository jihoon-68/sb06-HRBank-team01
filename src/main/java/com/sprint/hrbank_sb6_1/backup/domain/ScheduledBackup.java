package com.sprint.hrbank_sb6_1.backup.domain;

import com.sprint.hrbank_sb6_1.domain.File;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_backup")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 백업 사용자
    @Column(nullable = false)
    private String worker;

    // 백업 시작
    @Column(nullable = false)
    private LocalDateTime startedAt;

    // 백업 종료
    private LocalDateTime endedAt;

    // 백업 상태
    @Column(nullable = false)
    private String status;

    // 백업 파일, 실패 로그 파일
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File backupFile;

    //상태 업데이트

    public void markInProgress(String worker) {
        this.worker = worker;
        this.status = "IN_PROGRESS";
        this.startedAt = LocalDateTime.now();
    }

    public void markCompleted(File csvFile) {
        this.status = "COMPLETE";
        this.endedAt = LocalDateTime.now();
        this.backupFile = csvFile;
    }

    public void markFailed(File logFile) {
        this.status = "FAILED";
        this.endedAt = LocalDateTime.now();
        this.backupFile = logFile;
    }

    public void markSkipped() {
        this.status = "SKIPPED";
        this.endedAt = LocalDateTime.now();
    }
}