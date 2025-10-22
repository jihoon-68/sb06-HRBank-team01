package com.sprint.hrbank_sb6_1.dto;

import java.time.LocalDate;

public record DepartmentUpdateRequest(
  String name,
  String description,
  LocalDate establishedDate
) {}
