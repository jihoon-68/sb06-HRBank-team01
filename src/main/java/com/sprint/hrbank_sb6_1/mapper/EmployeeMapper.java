package com.sprint.hrbank_sb6_1.mapper;

import com.sprint.hrbank_sb6_1.domain.Employee;
import com.sprint.hrbank_sb6_1.domain.File;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(target = "profileImageId", expression = "java(getProfileImageId(employee.getProfileImage()))")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "hireDate", expression = "java(employee.getHireDate().toString())")
    @Mapping(target = "status", expression = "java(employee.getStatus().name())")
    EmployeeDto toDto(Employee employee);

    default Long getProfileImageId(File profile) {
        return profile != null ? profile.getId() : null;
    }
}
