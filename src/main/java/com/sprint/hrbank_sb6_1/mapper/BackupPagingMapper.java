package com.sprint.hrbank_sb6_1.mapper;

import com.sprint.hrbank_sb6_1.domain.Backup;
import com.sprint.hrbank_sb6_1.dto.CursorPageBackupDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponseBackupDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring",uses = BackupMapper.class)
public interface BackupPagingMapper {

    @Mapping(target = "hasNext" ,expression = "java(slice.hasNext())")
    @Mapping(target = "content", defaultExpression = "java(new java.util.ArrayList<>())")
    CursorPageResponseBackupDto toCursorPageResponseBackupDto(Slice<Backup> slice, CursorPageBackupDto cursorPageBackupDto);
}