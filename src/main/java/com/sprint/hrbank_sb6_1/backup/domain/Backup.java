package com.sprint.hrbank_sb6_1.backup.domain;

import com.sprint.hrbank_sb6_1.domain.File;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "backup")
public class Backup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String worker;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BackupStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File backupFile;

    public Backup() {}


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getWorker() { return worker; }
    public void setWorker(String worker) { this.worker = worker; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public BackupStatus getStatus() { return status; }
    public void setStatus(BackupStatus status) { this.status = status; }
    public File getBackupFile() { return backupFile; }
    public void setBackupFile(File backupFile) { this.backupFile = backupFile; }
}