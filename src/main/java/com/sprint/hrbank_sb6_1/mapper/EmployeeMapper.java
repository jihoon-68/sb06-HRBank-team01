package com.sprint.hrbank_sb6_1.mapper;

import com.sprint.hrbank_sb6_1.domain.Employee;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeDto toDto(Employee employee) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .departmentId(employee.getDepartment().getId())
                .departmentName(employee.getDepartment().getName())
                .employeeNumber(employee.getEmployeeNumber())
                .email(employee.getEmail())
                .name(employee.getName())
                .hireDate(employee.getHireDate().toString())
                .position(employee.getPosition())
                .status(employee.getStatus().getDescription())
                .profileImageId(employee.getProfileImage().getId())
                .build();
    }
}
