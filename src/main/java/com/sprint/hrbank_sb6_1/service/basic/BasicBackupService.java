package com.sprint.hrbank_sb6_1.service.basic;

import com.sprint.hrbank_sb6_1.domain.Backup;
import com.sprint.hrbank_sb6_1.domain.BackupStatus;
import com.sprint.hrbank_sb6_1.dto.BackupDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponseDepartmentDto;
import com.sprint.hrbank_sb6_1.dto.SearchBackupRequest;
import com.sprint.hrbank_sb6_1.event.BackupEvent;
import com.sprint.hrbank_sb6_1.mapper.BackupMapper;
import com.sprint.hrbank_sb6_1.mapper.BackupPagingMapper;
import com.sprint.hrbank_sb6_1.repository.BackupRepository;
import com.sprint.hrbank_sb6_1.repository.ChangeLogRepository;
import com.sprint.hrbank_sb6_1.service.BackupService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@AllArgsConstructor
public class BasicBackupService implements BackupService {
    private final BackupRepository backupRepository;
    private final BackupMapper backupMapper;
    private final BackupPagingMapper backupPagingMapper;
    private final ChangeLogRepository changeLogRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public BackupDto CreateBackup(String userIp) {

        //백업상태가 성공인 데이터 중에 최근 데이터 조회
        Backup lastBackup = backupRepository.findTopByStatusOrderByStartedAtDesc(BackupStatus.COMPLETED)
                .orElse(null);

        //최근 성공한 백엡시간이 null이면 성공한 백업이 없다는 뜻
        Long employeeChangesCount =null;
        if(lastBackup != null) {
            employeeChangesCount = changeLogRepository.countByAtBetween(lastBackup.getStartedAt(), LocalDateTime.now());
        }

        Backup newBackup = new Backup();

        //벡업이 필요할때
        //성공한 백업이없을떄 또는 직원 수정이력이 마직막 성공 시간 이후에 10 개 이상 일떄
        if (employeeChangesCount == null || employeeChangesCount >= 10) {
            newBackup.setWorker(userIp);
            newBackup.setStatus(BackupStatus.IN_PROGRESS);
            backupRepository.save(newBackup);
            eventPublisher.publishEvent(new BackupEvent(newBackup.getId()));
            return backupMapper.toBackupDto(newBackup);
        }

        //백업이 필요없을때
        newBackup.setWorker(userIp);
        newBackup.setStatus(BackupStatus.SKIPPED);
        backupRepository.save(newBackup);
        return backupMapper.toBackupDto(newBackup);
    }

    @Override
    public CursorPageResponseDepartmentDto GetAllBackups(SearchBackupRequest searchBackupRequest) {
        long totalCount = backupRepository.countTasks(searchBackupRequest);
        Slice<Backup> backupSlice = backupRepository.searchTasks(searchBackupRequest);
        Slice<BackupDto> backupDtoSlice = backupSlice.map(backupMapper::toBackupDto);
        return backupPagingMapper.toCursorPageResponseDepartmentDto(backupDtoSlice,totalCount);

    }

    @Override
    public BackupDto GetBackupByStatus(BackupStatus status) {
        Backup backup =backupRepository.findTopByStatusOrderByStartedAtDesc(status).orElse(null);
        return backupMapper.toBackupDto(backup);
    }
}
