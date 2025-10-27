package com.sprint.hrbank_sb6_1.dto;

import com.sprint.hrbank_sb6_1.domain.Department;
import java.time.LocalDate;

public record DepartmentResponse(
    Long id,
    String name,
    String description,
    LocalDate establishedDate,
    Integer employeeCount
) {
  public static DepartmentResponse of(Department d, int employeeCount) {
    return new DepartmentResponse(
        d.getId(), d.getName(), d.getDescription(), d.getEstablishedDate(), employeeCount
    );
  }

  public static DepartmentResponse from(Department d) {
    return new DepartmentResponse(
        d.getId(), d.getName(), d.getDescription(), d.getEstablishedDate(), 0
    );
  }
}