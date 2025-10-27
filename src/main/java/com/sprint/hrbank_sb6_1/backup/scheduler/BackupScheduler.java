package com.sprint.hrbank_sb6_1.backup.scheduler;

import com.sprint.hrbank_sb6_1.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackupScheduler {

    private final BackupService backupService;

    @Scheduled(cron = "${backup.schedule.cron:0 * * * * *}")
    public void executeBackup() {
        log.info("🔁 자동 백업 스케줄러 시작");
        try {
            backupService.CreateBackup("system");
            log.info("백업 완료 ✅");
        } catch (Exception e) {
            log.error("백업 실패 ❌", e);
        }
    }
}