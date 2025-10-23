package com.sprint.hrbank_sb6_1.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "backup_task")
public class BackupTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String worker;

    // 백업 이름
    @Column(nullable = false)
    private String name;

    // 백업 상태 (PENDING, IN_PROGRESS, COMPLETED 등)
    @Column(nullable = false)
    private String status;

    // 생성 시각 (자동 세팅)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 완료 시각
    private LocalDateTime completedAt;

    // File 엔티티 양방향
    @OneToMany(mappedBy = "backupTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    // 엔티티 저장 전 실행
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING"; // 기본 상태
    }

    // 백업 완료 시 호출할 메서드
    public void markCompleted() {
        this.status = "COMPLETED";
        this.completedAt = LocalDateTime.now();
    }
}