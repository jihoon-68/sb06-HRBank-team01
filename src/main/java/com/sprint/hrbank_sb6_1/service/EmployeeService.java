package com.sprint.hrbank_sb6_1.service;

import com.sprint.hrbank_sb6_1.dto.BinaryContentCreateRequest;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponse;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeDistributionDto;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeTrendDto;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeCreateRequest;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeDto;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeFindAllRequest;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    EmployeeDto create(String ip, EmployeeCreateRequest employeeCreateRequest, Optional<BinaryContentCreateRequest> optionalFileCreateRequest);

    EmployeeDto update(String ip, Long employeeId, EmployeeUpdateRequest employeeUpdateRequest, Optional<BinaryContentCreateRequest> optionalFileCreateRequest);

    void delete(String ip, Long id);

    EmployeeDto findById(Long id);

    CursorPageResponse<EmployeeDto> findAll(EmployeeFindAllRequest employeeFindAllRequest);

    List<EmployeeTrendDto> searchTrend(String from, String to, String unit);

    List<EmployeeDistributionDto> searchDistribution(String groupBy, String status);
}
