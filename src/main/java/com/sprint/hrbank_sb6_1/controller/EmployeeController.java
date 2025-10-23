package com.sprint.hrbank_sb6_1.controller;

import com.sprint.hrbank_sb6_1.dto.BinaryContentCreateRequest;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponse;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeCreateRequest;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeDto;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeFindAllRequest;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeUpdateRequest;
import com.sprint.hrbank_sb6_1.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeDto> createEmployee(HttpServletRequest httpServletRequest,
                                                      @RequestPart("employee") EmployeeCreateRequest employeeCreateRequest,
                                                      @RequestPart(value = "profile", required = false) MultipartFile profile) {
        Optional<BinaryContentCreateRequest> binaryContentCreateRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);
        EmployeeDto employeeDto = employeeService.create(getClientIp(httpServletRequest), employeeCreateRequest, binaryContentCreateRequest);
        return ResponseEntity.ok().body(employeeDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable(name = "id") Long employeeId,
                                                      @RequestPart("employee") EmployeeUpdateRequest employeeUpdateRequest,
                                                      @RequestPart(value = "profile", required = false) MultipartFile profile,
                                                      HttpServletRequest httpServletRequest) {
        Optional<BinaryContentCreateRequest> fileCreateRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);
        EmployeeDto employeeDto = employeeService.update(getClientIp(httpServletRequest), employeeId, employeeUpdateRequest, fileCreateRequest);
        return ResponseEntity.ok().body(employeeDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(HttpServletRequest httpServletRequest, @PathVariable(name = "id") Long id) {
        employeeService.delete(getClientIp(httpServletRequest), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable(name = "id") Long id) {
        EmployeeDto employeeDto = employeeService.findById(id);
        return ResponseEntity.ok().body(employeeDto);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponse<EmployeeDto>> getAllEmployees(EmployeeFindAllRequest employeeFindAllRequest) {
        CursorPageResponse<EmployeeDto> cursorPageResponse = employeeService.findAll(employeeFindAllRequest);
        return  ResponseEntity.ok().body(cursorPageResponse);
    }

    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
        if (profileFile.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                BinaryContentCreateRequest fileCreateRequest =
                        BinaryContentCreateRequest.builder()
                                .fileName(profileFile.getOriginalFilename())
                                .bytes(profileFile.getBytes())
                                .contentType(profileFile.getContentType())
                                .build();
                return Optional.of(fileCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
