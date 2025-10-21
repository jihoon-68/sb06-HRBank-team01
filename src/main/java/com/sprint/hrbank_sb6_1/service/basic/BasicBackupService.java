package com.sprint.hrbank_sb6_1.service.basic;

import com.sprint.hrbank_sb6_1.domain.Backup;
import com.sprint.hrbank_sb6_1.domain.BackupStatus;
import com.sprint.hrbank_sb6_1.dto.BackupDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponseDepartmentDto;
import com.sprint.hrbank_sb6_1.dto.SearchBackupRequest;
import com.sprint.hrbank_sb6_1.mapper.BackupMapper;
import com.sprint.hrbank_sb6_1.mapper.PagingMapper;
import com.sprint.hrbank_sb6_1.repository.BackupRepository;
import com.sprint.hrbank_sb6_1.service.BackupService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@AllArgsConstructor
public class BasicBackupService implements BackupService {
    private final BackupRepository backupRepository;
    private final BackupMapper backupMapper;
    //생성되면 다시 수정 예정
    //private final ChangeLogRepository changeLogRepository;
    private final PagingMapper pagingMapper;

    @Override
    public BackupDto CreateBackup(String userIp) {

        //백업상태가 성공인 데이터 중에 최근 데이터 조회
        Backup lastBackup = backupRepository.findTopByStatusOrderByStartedAtDesc(BackupStatus.COMPLETED)
                .orElse(null);

        //최근 성공한 백엡시간이 null이면 상공한 백업 이 없다는 뜻
        LocalDateTime lastBackupAt = lastBackup != null ? lastBackup.getStartedAt() : null;

        //직원 정보 이력 레포에서 lastBackupAt 시간 이후에 유저내용 변경내용 몇개 있는지 카운팅
        //ChangeLogRepository 쿼리 요청 예정
        int employeeChangesCount = 0;

        Backup newBackup = new Backup();

        //벡업이 필요할때
        if (lastBackupAt == null || employeeChangesCount > 10) {
            newBackup.setWorker(userIp);
            newBackup.setStatus(BackupStatus.PROGRESS);
            backupRepository.save(newBackup);
            return backupMapper.toBackupDto(newBackup);
        }

        //백업이 필요없을때
        newBackup.setWorker(userIp);
        newBackup.setStatus(BackupStatus.SKIPPED);
        backupRepository.save(newBackup);
        return backupMapper.toBackupDto(newBackup);
    }

    @Override
    public CursorPageResponseDepartmentDto<BackupDto> GetAllBackups(SearchBackupRequest searchBackupRequest) {
        long totalCount = backupRepository.countTasks(searchBackupRequest);
        Slice<Backup> backupSlice = backupRepository.searchTasks(searchBackupRequest);
        Slice<BackupDto> backupDtoSlice = backupSlice.map(backupMapper::toBackupDto);
        return pagingMapper.toCursorPageResponseDepartmentDto(backupDtoSlice,totalCount);

    }

    @Override
    public BackupDto GetBackupByStatus(BackupStatus status) {
        Backup backup =backupRepository.findTopByStatusOrderByStartedAtDesc(status).orElse(null);
        return backupMapper.toBackupDto(backup);
    }
}
