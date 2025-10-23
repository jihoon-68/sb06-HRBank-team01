package com.sprint.hrbank_sb6_1.backup.service;

import com.sprint.hrbank_sb6_1.backup.repository.BackupTaskRepository;
import com.sprint.hrbank_sb6_1.domain.BackupTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BackupTaskService {

    private final BackupTaskRepository backupTaskRepository;

    @Transactional(readOnly = true)
    public List<BackupTask> getAllTasks() {
        return backupTaskRepository.findAll();
    }

    @Transactional
    public BackupTask createTask(String name) {
        BackupTask task = new BackupTask();
        task.setName(name);
        task.setStatus("PENDING");
        return backupTaskRepository.save(task);
    }

    @Transactional
    public BackupTask completeTask(Long id) {
        BackupTask task = backupTaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("백업 작업을 찾을 수 없습니다."));
        task.markCompleted();
        return backupTaskRepository.save(task);
    }
}