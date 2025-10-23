package com.sprint.hrbank_sb6_1.repository;

import com.sprint.hrbank_sb6_1.domain.Employee;
import com.sprint.hrbank_sb6_1.domain.EmployeeStatus;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponse;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeFindAllRequest;

public interface EmployeeRepositoryCustom {
    CursorPageResponse<Employee> findAll(EmployeeFindAllRequest request);
}
