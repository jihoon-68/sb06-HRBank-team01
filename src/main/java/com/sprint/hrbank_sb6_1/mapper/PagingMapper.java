package com.sprint.hrbank_sb6_1.mapper;

import com.sprint.hrbank_sb6_1.dto.BackupDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponseDepartmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
public interface PagingMapper {

    @Mapping(target = "nextCursor", expression = "java(slice.getContent().get(slice.getContent().size()-1).getStartedAt().toString())")
    @Mapping(target = "nextIdAfter",expression = "java(slice.getContent().get(slice.getContent().size()-1).getId())")
    @Mapping(source = "totalElements",target = "totalElements")
    CursorPageResponseDepartmentDto<BackupDto> toCursorPageResponseDepartmentDto(Slice<BackupDto> slice, Long totalElements);
}
