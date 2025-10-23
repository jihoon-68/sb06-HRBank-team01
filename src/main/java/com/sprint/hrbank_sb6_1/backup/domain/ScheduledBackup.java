package com.sprint.hrbank_sb6_1.backup.domain;

import com.sprint.hrbank_sb6_1.domain.File;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scheduled_backup")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 백업 수행 사용자
    @Column(nullable = false)
    private String worker;

    // 백업 시작 시간
    @Column(nullable = false)
    private LocalDateTime startedAt;

    // 백업 종료 시간
    private LocalDateTime endedAt;

    // 백업 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduledBackupStatus status;

    // 백업 결과로 생성된 파일 목록
    @OneToMany(mappedBy = "backupTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    public void addFile(File file) {
        this.files.add(file);
        file.setBackupTask(this);
    }
}