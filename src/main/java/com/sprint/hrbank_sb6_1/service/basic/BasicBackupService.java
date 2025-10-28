package com.sprint.hrbank_sb6_1.service.basic;

import com.sprint.hrbank_sb6_1.domain.Backup;
import com.sprint.hrbank_sb6_1.domain.BackupStatus;
import com.sprint.hrbank_sb6_1.domain.File;
import com.sprint.hrbank_sb6_1.dto.BackupDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageBackupDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponseBackupDto;
import com.sprint.hrbank_sb6_1.dto.request.SearchBackupRequest;
import com.sprint.hrbank_sb6_1.event.BackupEvent;
import com.sprint.hrbank_sb6_1.event.BackupIoEvent;
import com.sprint.hrbank_sb6_1.mapper.BackupMapper;
import com.sprint.hrbank_sb6_1.mapper.BackupPagingMapper;
import com.sprint.hrbank_sb6_1.repository.BackupRepository;
import com.sprint.hrbank_sb6_1.repository.ChangeLogRepository;
import com.sprint.hrbank_sb6_1.repository.FileRepository;
import com.sprint.hrbank_sb6_1.service.BackupService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
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
    private final FileRepository fileRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public BackupDto CreateBackup( String userIp) {
        //백업상태가 성공인 데이터 중에 최근 데이터 조회
        Backup lastCompletedBackup = backupRepository.findTopByStatusOrderByStartedAtDesc(BackupStatus.COMPLETED)
                .orElse(null);

        //최근 성공한 백엡시간이 null이면 성공한 백업이 없다는 뜻
        long employeeChangesCount =0L;
        if(lastCompletedBackup != null) {
            employeeChangesCount = changeLogRepository.countByAtBetween(lastCompletedBackup.getStartedAt(), LocalDateTime.now());
        }else {
            employeeChangesCount = changeLogRepository.count();
        }

        Backup newBackup = new Backup();

        //벡업이 필요할때
        //성공한 백업이없을떄 또는 직원 수정이력이 마직막 성공 시간 이후에 10 개 이상 일떄
        if (employeeChangesCount >10) {
            newBackup.markInProgress(userIp);
            backupRepository.save(newBackup);
            eventPublisher.publishEvent(new BackupEvent(newBackup.getId()));
            return backupMapper.toBackupDto(newBackup);
        }

        //백업이 필요없을때
        newBackup.markSkipped(userIp);
        backupRepository.save(newBackup);
        return backupMapper.toBackupDto(newBackup);
    }

    @Override
    public CursorPageResponseBackupDto GetAllBackups(SearchBackupRequest searchBackupRequest) {
        long totalCount = backupRepository.countTasks(searchBackupRequest);
        Slice<Backup> backupSlice = backupRepository.searchTasks(searchBackupRequest);

        String backupStartTime =null;
        Long backId =null;
        if(!backupSlice.getContent().isEmpty()&&backupSlice.hasNext()) {
            backupStartTime =  backupSlice.getContent().get(0).getStartedAt().toString();
            backId = backupSlice.getContent().get(0).getId();
        }

        CursorPageBackupDto cursorPageBackupDto = new CursorPageBackupDto(backupStartTime, backId,totalCount);

        return backupPagingMapper.toCursorPageResponseBackupDto(backupSlice,cursorPageBackupDto);

    }

    @Override
    public BackupDto GetBackupByStatus(BackupStatus status) {
        Backup backup =backupRepository.findTopByStatusOrderByStartedAtDesc(status).orElse(null);
        return backupMapper.toBackupDto(backup);
    }

    @EventListener
    @Transactional
    protected void setBackupStatus(BackupIoEvent backupIoEvent) {
        Backup backup= backupIoEvent.backup();
        //BackupIo 받아온 파일 정보로 파일 객체 생성
        File file = new File(null , backupIoEvent.fileName() , backupIoEvent.type() , backupIoEvent.size());

        //err여부로 백업 상태 변경 과 파일 추가
        if(backupIoEvent.err()){
            backup.markFailed(file);
        }else {
           backup.markCompleted(file);
        }

        //백업, 파일정보 저장
        backupRepository.save(backup);
        fileRepository.save(file);
    }
}