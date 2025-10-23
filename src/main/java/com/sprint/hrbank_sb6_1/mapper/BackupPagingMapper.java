package com.sprint.hrbank_sb6_1.mapper;

import com.sprint.hrbank_sb6_1.dto.BackupDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageBackupDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponseBackupDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
public interface BackupPagingMapper {

    @Mapping(target = "nextCursor", expression = "java(cursorPageBackupDto.getNextCursor())")
    @Mapping(target = "nextIdAfter",expression = "java(cursorPageBackupDto.getNextIdAfter())")
    @Mapping(target = "content" ,expression = "java(slice.getContent())")
    @Mapping(target = "totalElements",expression = "java(cursorPageBackupDto.getTotalElements())")
    CursorPageResponseBackupDto toCursorPageResponseBackupDto(Slice<BackupDto> slice, CursorPageBackupDto cursorPageBackupDto);
}
