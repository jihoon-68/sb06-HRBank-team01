package com.sprint.hrbank_sb6_1.service.basic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.hrbank_sb6_1.domain.*;
import com.sprint.hrbank_sb6_1.dto.BinaryContentCreateRequest;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponse;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeDto;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeTrendDto;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeCreateRequest;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeFindAllRequest;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeUpdateRequest;
import com.sprint.hrbank_sb6_1.mapper.EmployeeMapper;
import com.sprint.hrbank_sb6_1.repository.ChangeLogRepository;
import com.sprint.hrbank_sb6_1.repository.DepartmentRepository;
import com.sprint.hrbank_sb6_1.repository.EmployeeRepository;
import com.sprint.hrbank_sb6_1.repository.FileRepository;
import com.sprint.hrbank_sb6_1.service.EmployeeService;
import com.sprint.hrbank_sb6_1.service.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicEmployeeService implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final FileRepository fileRepository;
    private final EmployeeMapper employeeMapper;
    private final ChangeLogRepository changeLogRepository;
    private final ObjectMapper objectMapper;
    private final FileStorage fileStorage;

    @Transactional
    @Override
    public EmployeeDto create(String ip, EmployeeCreateRequest employeeCreateRequest, Optional<BinaryContentCreateRequest> optionalBinaryContentCreateRequest) {
        if (employeeRepository.existsByEmail(employeeCreateRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }

        Department department = departmentRepository.findById(employeeCreateRequest.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException("부서를 찾을 수 없습니다."));

        File nullableProfile = optionalBinaryContentCreateRequest.map(
                binaryContentCreateRequest -> {
                    File profile = new File();
                    profile.setName(binaryContentCreateRequest.fileName());
                    profile.setSize(binaryContentCreateRequest.bytes().length);
                    profile.setType(binaryContentCreateRequest.contentType());
                    File createdFile = fileRepository.save(profile);
                    fileStorage.putFile(binaryContentCreateRequest.bytes(), createdFile.getName());
                    return createdFile;
                }
        ).orElse(null);

        Employee newEmployee = Employee.builder()
                .employeeNumber(employeeNumberGenerator())
                .email(employeeCreateRequest.getEmail())
                .name(employeeCreateRequest.getName())
                .department(department)
                .hireDate(LocalDate.parse(employeeCreateRequest.getHireDate()))
                .position(employeeCreateRequest.getPosition())
                .status(EmployeeStatus.ACTIVE)
                .profileImage(nullableProfile)
                .build();

        Employee createdEmployee = employeeRepository.save(newEmployee);


        changeLog(ip, ChangeLogStatus.CREATED, employeeCreateRequest.getMemo(), new Employee(), createdEmployee);
        return employeeMapper.toDto(createdEmployee);
    }

    @Transactional
    @Override
    public EmployeeDto update(String ip, Long employeeId, EmployeeUpdateRequest employeeUpdateRequest, Optional<BinaryContentCreateRequest> optionalBinaryContentCreateRequest) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new NoSuchElementException("직원을 찾을 수 없습니다."));

        Employee before = Employee.builder()
                .name(employee.getName())
                .email(employee.getEmail())
                .position(employee.getPosition())
                .hireDate(employee.getHireDate())
                .status(employee.getStatus())
                .department(employee.getDepartment())
                .profileImage(employee.getProfileImage())
                .build();

        if (!employee.getEmail().equals(employeeUpdateRequest.getEmail())
                && employeeRepository.existsByEmail(employeeUpdateRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }

        Department department = departmentRepository.findById(employeeUpdateRequest.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException("부서를 찾을 수 없습니다."));

        File nullableProfile = optionalBinaryContentCreateRequest.map(binaryContentCreateRequest -> {
            File profile = new File();
            profile.setType(binaryContentCreateRequest.contentType());
            profile.setName(binaryContentCreateRequest.fileName());
            profile.setSize(binaryContentCreateRequest.bytes().length);
            if (employee.getProfileImage() != null) {
                fileRepository.deleteById(employee.getProfileImage().getId());
            }
            File createdFile = fileRepository.save(profile);
            fileStorage.putFile(binaryContentCreateRequest.bytes(), createdFile.getName());
            return createdFile;
        }).orElse(employee.getProfileImage());

        employee.update(department, employeeUpdateRequest.getName(), employeeUpdateRequest.getPosition(),
                employeeUpdateRequest.getEmail(), EmployeeStatus.fromDescription(employeeUpdateRequest.getStatus()), nullableProfile,
                LocalDate.parse(employeeUpdateRequest.getHireDate()));

        Employee updatedEmployee = employeeRepository.save(employee);

        changeLog(ip, ChangeLogStatus.UPDATED, employeeUpdateRequest.getMemo(), before, updatedEmployee);

        return employeeMapper.toDto(updatedEmployee);
    }

    @Transactional
    @Override
    public void delete(String ip, Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("직원을 찾을 수 없습니다."));
        if (employee.getProfileImage() != null) {
            fileRepository.delete(employee.getProfileImage());
        }
        employeeRepository.delete(employee);
        changeLog(ip, ChangeLogStatus.DELETED, "직원 삭제", employee, new Employee());
    }

    @Override
    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("직원을 찾을 수 없습니다."));
        return employeeMapper.toDto(employee);
    }

    @Override
    public CursorPageResponse<EmployeeDto> findAll(EmployeeFindAllRequest employeeFindAllRequest) {
        return employeeRepository.findAll(employeeFindAllRequest).map(employeeMapper::toDto);
    }

    @Override
    public List<EmployeeTrendDto> searchTrend(String from, String to, String unit) {
        long minusValue = 12;
        to = to != null ? to : LocalDate.now().toString();
        unit = unit != null ? unit : "month";
        from = from != null ? from : switch (unit) {
            case "day" -> LocalDate.now().minusDays(minusValue).toString();
            case "week" -> LocalDate.now().minusWeeks(minusValue).toString();
            case "quarter" -> LocalDate.now().minusMonths(minusValue * 3).toString();
            case "year" -> LocalDate.now().minusYears(minusValue).toString();
            default -> LocalDate.now().minusMonths(minusValue).toString();
        };

        //between(from, to) 날짜순 정렬(asc)
        List<EmployeeTrendDto> employeeTrends = employeeRepository.getTrend(LocalDate.parse(from), LocalDate.parse(to), unit);
        if (!employeeTrends.isEmpty()) {
            employeeTrends.forEach(employeeTrend -> {
                if (employeeTrends.indexOf(employeeTrend) == 0) {
                    employeeTrend.setChange(0);
                    employeeTrend.setChangeRate(0.0);
                }
                if (employeeTrends.indexOf(employeeTrend) > 0) {
                    EmployeeTrendDto before = employeeTrends.get(employeeTrends.indexOf(employeeTrend) - 1);
                    employeeTrend.setChange((int) (employeeTrend.getCount() - before.getCount()));
                    employeeTrend.setChangeRate(employeeTrend.getChange() == 0 ?
                            0.0 : Math.round((double)employeeTrend.getChange() / before.getCount() * 100 * 10) / 10);
                }
            });
        }

        return employeeTrends;
    }

    private void changeLog(String ip, ChangeLogStatus changeLogStatus, String memo, Employee before, Employee after) {
        try {
            List<Map<String, String>> changeLogList = new ArrayList<>();

            if (!Objects.equals(before.getName(), after.getName())) {
                changeLogList.add(Map.of(
                        "propertyName", "이름",
                        "before", before.getName() != null ? before.getName() : "-",
                        "after", after.getName() != null ? after.getName() : "-"
                ));
            }

            if (!Objects.equals(before.getEmail(), after.getEmail())) {
                changeLogList.add(Map.of(
                        "propertyName", "이메일",
                        "before", before.getEmail() != null ? before.getEmail() : "-",
                        "after", after.getEmail() != null ? after.getEmail() : "-"
                ));
            }

            if (!Objects.equals(before.getPosition(), after.getPosition())) {
                changeLogList.add(Map.of(
                        "propertyName", "직함",
                        "before", before.getPosition() != null ? before.getPosition() : "-",
                        "after", after.getPosition() != null ? after.getPosition() : "-"
                ));
            }

            if (!Objects.equals(before.getHireDate(), after.getHireDate())) {
                changeLogList.add(Map.of(
                        "propertyName", "입사일",
                        "before", before.getHireDate() != null ? before.getHireDate().toString() : "-",
                        "after", after.getHireDate() != null ? after.getHireDate().toString() : "-"
                ));
            }

            if (!Objects.equals(before.getStatus(), after.getStatus())) {
                changeLogList.add(Map.of(
                        "propertyName", "상태",
                        "before", before.getStatus() != null ? before.getStatus().getDescription() : "-",
                        "after", after.getStatus() != null ? after.getStatus().getDescription() : "-"
                ));
            }

            if (!Objects.equals(
                    before.getDepartment() != null ? before.getDepartment().getId() : null,
                    after.getDepartment() != null ? after.getDepartment().getId() : null
            )) {
                changeLogList.add(Map.of(
                        "propertyName", "부서",
                        "before", before.getDepartment() != null ? before.getDepartment().getName() : "-",
                        "after", after.getDepartment() != null ? after.getDepartment().getName() : "-"
                ));
            }

            if (!Objects.equals(
                    before.getProfileImage() != null ? before.getProfileImage().getId() : null,
                    after.getProfileImage() != null ? after.getProfileImage().getId() : null
            )) {
                changeLogList.add(Map.of(
                        "propertyName", "프로필",
                        "before", before.getProfileImage() != null ? before.getProfileImage().getName() : "-",
                        "after", after.getProfileImage() != null ? after.getProfileImage().getName() : "-"
                ));
            }

            String json = objectMapper.writeValueAsString(changeLogList);
            Employee changeLogEmployee = changeLogStatus == ChangeLogStatus.DELETED ? null : after;

            ChangeLog changeLog = ChangeLog.builder()
                    .memo(memo)
                    .at(LocalDateTime.now())
                    .address(ip)
                    .employee(changeLogEmployee)
                    .description(json)
                    .status(changeLogStatus)
                    .build();

            changeLogRepository.save(changeLog);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("직원 변경 이력 작성에 실패했습니다.");
        }
    }

    private String employeeNumberGenerator() {
        Long recentlyId = employeeRepository.findAll().stream().sorted(Comparator.comparing(Employee::getId).reversed())
                .findFirst().map(Employee::getId).orElse(0L);

        int year = LocalDate.now().getYear();

        return String.format("EMP-%d-%03d", year, recentlyId + 1);
    }
}
