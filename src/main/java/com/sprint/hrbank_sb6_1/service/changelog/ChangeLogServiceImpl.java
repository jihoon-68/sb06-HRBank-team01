package com.sprint.hrbank_sb6_1.service.changelog;

import com.sprint.hrbank_sb6_1.domain.ChangeLog;
import com.sprint.hrbank_sb6_1.domain.ChangeLogStatus;
import com.sprint.hrbank_sb6_1.dto.ChangeLogDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponseChangeLogDto;
import com.sprint.hrbank_sb6_1.repository.ChangeLogRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;

    @Override
    public CursorPageResponseChangeLogDto getChangeLog(
        String employeeNumber,
        String type,
        String memo,
        String ipAddress,
        LocalDateTime atFrom,
        LocalDateTime atTo,
        Long idAfter,
        int size,
        String sortField,
        String sortDirection
    ) {
        Sort.Direction direction =
            "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(0, size, Sort.by(direction, sortField));

        // Enum 변환
        ChangeLogStatus status = (type != null && !type.isBlank())
            ? ChangeLogStatus.valueOf(type)
            : null;

        List<ChangeLog> logs = changeLogRepository.searchChangeLogs(
            employeeNumber,
            status,
            memo,
            ipAddress,
            atFrom,
            atTo,
            idAfter,
            pageable
        );

        boolean hasNext = logs.size() == size;
        Long nextIdAfter = hasNext ? logs.get(logs.size() - 1).getId() : null;

        List<ChangeLogDto> content = logs.stream()
            .map(ChangeLogDto::from)
            .toList();

        return new CursorPageResponseChangeLogDto(
            content,
            nextIdAfter != null ? String.valueOf(nextIdAfter) : null,
            nextIdAfter != null ? nextIdAfter.intValue() : 0,
            size,
            content.size(),
            hasNext
        );
    }


}
