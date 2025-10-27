package com.sprint.hrbank_sb6_1.service.changelog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.hrbank_sb6_1.domain.ChangeLog;
import com.sprint.hrbank_sb6_1.dto.ChangeLogDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponseChangeLogDto;
import com.sprint.hrbank_sb6_1.dto.DiffDto;
import com.sprint.hrbank_sb6_1.repository.ChangeLogRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;

    @Override
    public CursorPageResponseChangeLogDto getChangeLog(
        String employeeNumber,
        int type,
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

        List<ChangeLog> logs = changeLogRepository.searchChangeLogs(
            employeeNumber,
            type,
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

    @Override
    public List<DiffDto> getChangeLogDiffs(Long changeLogId) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ChangeLog changeLog = changeLogRepository.findById(changeLogId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "ChangeLog not found with id: " + changeLogId));

            return objectMapper.readValue(changeLog.getDescription(),
                new TypeReference<List<DiffDto>>() {
                });
        } catch (JsonProcessingException e){
            throw new RuntimeException("Failed to parse change log diffs", e);
        }

    }

    @Override
    public Long countChangeLogs(LocalDateTime fromDate, LocalDateTime toDate) {
        return changeLogRepository.countByAtBetween(fromDate, toDate);
    }

}
