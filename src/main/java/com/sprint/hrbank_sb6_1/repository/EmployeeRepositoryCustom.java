package com.sprint.hrbank_sb6_1.repository;

import com.sprint.hrbank_sb6_1.domain.Employee;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponse;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeTrendDto;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeFindAllRequest;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepositoryCustom {
    CursorPageResponse<Employee> findAll(EmployeeFindAllRequest request);

    List<EmployeeTrendDto> getTrend(LocalDate from, LocalDate to, String unit);
}
