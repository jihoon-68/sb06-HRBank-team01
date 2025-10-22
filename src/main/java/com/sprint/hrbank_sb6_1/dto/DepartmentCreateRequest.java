package com.sprint.hrbank_sb6_1.dto;

import java.sql.Timestamp;
import java.time.LocalDate;

public record DepartmentCreateRequest(
    String name,
    String description,
    LocalDate establishedDate
) {}