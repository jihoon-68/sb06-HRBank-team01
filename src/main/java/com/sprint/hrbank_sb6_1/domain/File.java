package com.sprint.hrbank_sb6_1.domain;


import com.sprint.hrbank_sb6_1.backup.scheduled.domain.ScheduledBackup;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int size;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduled_backup_id")
    private ScheduledBackup backupTask;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    }