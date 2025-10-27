package com.sprint.hrbank_sb6_1.service.changelog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.hrbank_sb6_1.domain.ChangeDiff;
import com.sprint.hrbank_sb6_1.domain.ChangeLog;
import com.sprint.hrbank_sb6_1.dto.ChangeLogDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponseChangeLogDto;
import com.sprint.hrbank_sb6_1.dto.DiffDto;
import com.sprint.hrbank_sb6_1.repository.ChangeDiffRepository;
import com.sprint.hrbank_sb6_1.repository.ChangeLogRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final ChangeDiffRepository changeDiffRepository;

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
        ChangeLog changeLog = changeLogRepository.findById(changeLogId).orElseThrow(
            () -> new NoSuchElementException("invaild changeLog Id")
        );

        List<ChangeDiff> changeDiffs = changeDiffRepository.findByChangeLog(changeLog);

        List<DiffDto> diffDtos = changeDiffs.stream()
            .map(DiffDto::from)
            .toList();

        return diffDtos;
    }

    @Override
    public Long countChangeLogs(LocalDateTime fromDate, LocalDateTime toDate) {
        return changeLogRepository.countByAtBetween(fromDate, toDate);
    }

}
