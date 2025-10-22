package com.sprint.hrbank_sb6_1.mapper;

import com.sprint.hrbank_sb6_1.dto.CursorPageResponseDepartmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
public interface PagingMapper {

    @Mapping(target = "nextCursor", expression = "java(slice.getContent().get(dtoList.size()-1).getStartedAt())")
    @Mapping(target = "nextIdAfter",expression = "java(slice.getContent().get(dtoList.size()-1).getId())")
    @Mapping(source = "totalElements",target = "totalElements")
    <T> CursorPageResponseDepartmentDto<T> toCursorPageResponseDepartmentDto(Slice<T> slice,Long totalElements);
}
