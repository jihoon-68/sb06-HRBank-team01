package com.sprint.hrbank_sb6_1.service;

import com.sprint.hrbank_sb6_1.dto.request.EmployeeCreateRequest;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeDto;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeUpdateRequest;
import com.sprint.hrbank_sb6_1.dto.request.FileCreateRequest;

import java.util.Optional;

public interface EmployeeService {
    EmployeeDto create(String ip, EmployeeCreateRequest employeeCreateRequest, Optional<FileCreateRequest> optionalFileCreateRequest);

    EmployeeDto update(String ip, Long employeeId, EmployeeUpdateRequest employeeUpdateRequest, Optional<FileCreateRequest> optionalFileCreateRequest);
}
