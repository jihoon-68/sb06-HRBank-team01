package com.sprint.hrbank_sb6_1.backup.controller;

import com.sprint.hrbank_sb6_1.backup.service.BackupTaskService;
import com.sprint.hrbank_sb6_1.domain.BackupTask;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/backup-tasks")
@RequiredArgsConstructor
public class BackupTaskController {

    private final BackupTaskService backupTaskService;

    @GetMapping
    public ResponseEntity<List<BackupTask>> getAllTasks() {
        return ResponseEntity.ok(backupTaskService.getAllTasks());
    }

    @PostMapping
    public ResponseEntity<BackupTask> createTask(@RequestParam String name) {
        return ResponseEntity.ok(backupTaskService.createTask(name));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<BackupTask> completeTask(@PathVariable Long id) {
        return ResponseEntity.ok(backupTaskService.completeTask(id));
    }
}