package com.sprint.hrbank_sb6_1.dto.request;

import com.sprint.hrbank_sb6_1.domain.EmployeeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeFindAllRequest {
    private String nameOrEmail;
    private String employeeNumber;
    private String departmentName;
    private String position;
    private String hireDateFrom;
    private String hireDateTo;
    private EmployeeStatus status;
    private Long idAfter;
    private String cursor;
    private Integer size = 10;
    private String sortField = "name";
    private String sortDirection = "asc";
}
