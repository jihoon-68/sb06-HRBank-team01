package com.sprint.hrbank_sb6_1.backup.scheduler;

import com.sprint.hrbank_sb6_1.backup.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackupScheduler {

    private final BackupService backupService;

    @Scheduled(fixedRateString = "${backup.schedule.rate:3600000}")
    public void runScheduledBackup() {
        log.info("🔁 자동 백업 스케줄러 시작");
        try {
            backupService.performBackup();
            log.info("백업 완료 ✅");
        } catch (Exception e) {
            log.error("백업 실패 ❌", e);
        }
    }
}