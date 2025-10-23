package com.sprint.hrbank_sb6_1.controller;

import com.sprint.hrbank_sb6_1.domain.BackupStatus;
import com.sprint.hrbank_sb6_1.dto.BackupDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponseBackupDto;
import com.sprint.hrbank_sb6_1.dto.SearchBackupRequest;
import com.sprint.hrbank_sb6_1.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backups")
@AllArgsConstructor
public class BackupController {
    private final BackupService backupService;

    @GetMapping
    public ResponseEntity<CursorPageResponseBackupDto> getBackups(
            @ModelAttribute SearchBackupRequest searchBackupRequest
    ) {
        CursorPageResponseBackupDto pageResponseDepartmentDto = backupService.GetAllBackups(searchBackupRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pageResponseDepartmentDto);
    }

    @PostMapping
    public ResponseEntity<BackupDto> createBackup(HttpServletRequest request) {

        BackupDto backupDto = backupService.CreateBackup(request.getRemoteAddr());

        return ResponseEntity.status(HttpStatus.CREATED).body(backupDto);
    }

    @GetMapping("/latest")
    public ResponseEntity<BackupDto> getLatestBackup(
            @RequestParam(defaultValue = "COMPLETED") BackupStatus status) {
        BackupDto backupDto = backupService.GetBackupByStatus(status);
        return ResponseEntity.status(HttpStatus.OK).body(backupDto);
    }

}
