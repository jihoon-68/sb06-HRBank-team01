package com.sprint.hrbank_sb6_1.service.changelog;

import com.sprint.hrbank_sb6_1.dto.CursorPageResponseChangeLogDto;
import java.time.LocalDateTime;

public interface ChangeLogService {

    CursorPageResponseChangeLogDto getChangeLog(String employeeNumber, String type, String memo, String ipAddress, LocalDateTime atFrom, LocalDateTime atTo, Long idAfter, int size, String sortField, String sortDirection);


}
