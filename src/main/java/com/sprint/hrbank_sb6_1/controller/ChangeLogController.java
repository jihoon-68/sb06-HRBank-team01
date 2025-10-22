package com.sprint.hrbank_sb6_1.controller;

import com.sprint.hrbank_sb6_1.dto.CursorPageResponseChangeLogDto;
import com.sprint.hrbank_sb6_1.dto.DiffDto;
import com.sprint.hrbank_sb6_1.service.changelog.ChangeLogService;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/change-logs")
@RequiredArgsConstructor
public class ChangeLogController {

    private final ChangeLogService changeLogService;

    @GetMapping()
    public ResponseEntity<CursorPageResponseChangeLogDto> getChangeLogs(
        @RequestParam(required = false) String employeeNumber,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String memo,
        @RequestParam(required = false) String ipAddress,
        @RequestParam(required = false) String atFrom,
        @RequestParam(required = false) String atTo,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(defaultValue = "30") int size,
        @RequestParam(defaultValue = "at") String sortField,
        @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        CursorPageResponseChangeLogDto response = changeLogService.getChangeLog(
                employeeNumber, type, memo, ipAddress,
                atFrom != null ? java.time.LocalDateTime.parse(atFrom) : null,
                atTo != null ? java.time.LocalDateTime.parse(atTo) : null,
                idAfter, size, sortField, sortDirection
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/diffs")
    public ResponseEntity<DiffDto> getChangeLogDiffs(@PathVariable Long id) {
        DiffDto diffs = changeLogService.getChangeLogDiffs(id);
        return ResponseEntity.ok(diffs);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countChangeLogs(
        @RequestParam LocalDateTime fromDate, @RequestParam LocalDateTime toDate) {
        return ResponseEntity.ok(
            changeLogService.countChangeLogs(fromDate, toDate)
        );
    }
}
