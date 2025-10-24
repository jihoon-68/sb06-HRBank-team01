package com.sprint.hrbank_sb6_1.mapper;

import com.sprint.hrbank_sb6_1.domain.Backup;
import com.sprint.hrbank_sb6_1.dto.BackupDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BackupMapper {

    @Mapping(source = "file.id",target = "fileId")
    BackupDto toBackupDto(Backup backup);
}