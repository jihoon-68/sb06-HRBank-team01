package com.sprint.hrbank_sb6_1.dto.request;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateRequest {
    private String name;
    private String email;
    private Long departmentId;
    private String position;
    private String hireDate;
    private String memo;
}