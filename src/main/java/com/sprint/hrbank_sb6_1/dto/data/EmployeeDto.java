package com.sprint.hrbank_sb6_1.dto.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeDto {
    private Long id;
    private String name;
    private String email;
    private String employeeNumber;
    private Long departmentId;
    private String departmentName;
    private String position;
    private String hireDate;
    private String status;
    private Long profileImageId;
}
