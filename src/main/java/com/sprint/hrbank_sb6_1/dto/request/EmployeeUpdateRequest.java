package com.sprint.hrbank_sb6_1.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeUpdateRequest {
    private String name;
    private String email;
    private Long departmentId;
    private String position;
    private String hireDate;
    private String status;
    private String memo;
}