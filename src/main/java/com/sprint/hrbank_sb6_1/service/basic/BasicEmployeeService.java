package com.sprint.hrbank_sb6_1.service.basic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.hrbank_sb6_1.domain.*;
import com.sprint.hrbank_sb6_1.dto.BinaryContentCreateRequest;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponse;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeDistributionDto;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeDto;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeTrendDto;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeCreateRequest;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeFindAllRequest;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeUpdateRequest;
import com.sprint.hrbank_sb6_1.mapper.EmployeeMapper;
import com.sprint.hrbank_sb6_1.repository.ChangeDiffRepository;
import com.sprint.hrbank_sb6_1.repository.ChangeLogRepository;
import com.sprint.hrbank_sb6_1.repository.DepartmentRepository;
import com.sprint.hrbank_sb6_1.repository.EmployeeRepository;
import com.sprint.hrbank_sb6_1.repository.FileRepository;
import com.sprint.hrbank_sb6_1.service.EmployeeService;
import com.sprint.hrbank_sb6_1.service.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
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
    private final ChangeDiffRepository changeDiffRepository;

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
                fileRepository.delete(employee.getProfileImage());
            }
            File createdFile = fileRepository.save(profile);
            fileStorage.putFile(binaryContentCreateRequest.bytes(), createdFile.getName());
            return createdFile;
        }).orElse(employee.getProfileImage()); //null로 값을 줄 경우 프론트에서 계속 기존 이미지를 보내야하고 같은 이미지가 계속해서 새로 저장됌

        Employee before = employee.clone(); //준영속

        employee.update(department, employeeUpdateRequest.getName(), employeeUpdateRequest.getPosition(),
                employeeUpdateRequest.getEmail(), employeeUpdateRequest.getStatus(), nullableProfile,
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
        changeLog(ip, ChangeLogStatus.DELETED, "직원 삭제", employee, new Employee());
        employeeRepository.delete(employee);

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
        try {
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
                        employeeTrend.setChangeRate(employeeTrend.getChange() == 0 || before.getCount() == 0?
                                0.0 : Math.round((double) employeeTrend.getChange() / before.getCount() * 100 * 10) / 10);
                    }
                });
            }
            return employeeTrends;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식 입니다.");
        }
    }

    @Override
    public List<EmployeeDistributionDto> searchDistribution(String groupBy, String status) {
        List<EmployeeDistributionDto> employeeDistributions;
        if (groupBy.equalsIgnoreCase("position")) {
            employeeDistributions = employeeRepository.findDistributionByStatusGroupByPosition(EmployeeStatus.valueOf(status.toUpperCase()));
        } else {
            employeeDistributions = employeeRepository.findDistributionByStatusGroupByDepartment(EmployeeStatus.valueOf(status.toUpperCase()));
        }

        long sum = employeeDistributions.stream().mapToLong(EmployeeDistributionDto::getCount).sum();

        //sum이 0이면 리스트도 없지만 체크하는게 좋을 거라 생각
        if (sum > 0) {
            for (EmployeeDistributionDto employeeDistribution : employeeDistributions) {
                double percentage = Math.round((double) employeeDistribution.getCount() / sum * 100.0 * 10.0) / 10.0;
                employeeDistribution.setPercentage(percentage);
            }
        }

        return employeeDistributions;
    }

    @Override
    public Long getCount(EmployeeStatus status, String hireDateFrom, String hireDateTo) {
        try {
            LocalDate to = hireDateTo != null ? LocalDate.parse(hireDateTo) : LocalDate.now();
            LocalDate from = hireDateFrom != null ? LocalDate.parse(hireDateFrom) : null;

            return employeeRepository.getCount(status, from, to);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식 입니다.");
        }
    }

    private void changeLog(String ip, ChangeLogStatus changeLogStatus, String memo, Employee before, Employee after) {
        log.warn("changeLog 진입");
        List<Map<String, String>> changeLogList = new ArrayList<>();

        Employee changeLogEmployee = changeLogStatus.getDescription().equals("DELETED") ? null : after;
        ChangeLog changeLog = ChangeLog.builder()
            .memo(memo)
            .at(LocalDateTime.now())
            .address(ip)
            .employee(changeLogEmployee)
            .status(changeLogStatus.getCode())
            .build();
        changeLogRepository.save(changeLog);

        if (!Objects.equals(before.getName(), after.getName())) {
            ChangeDiff changeDiff = ChangeDiff.builder()
                .propertyName("이름")
                .oldValue(before.getName() != null ? before.getName() : "-")
                .newValue(after.getName() != null ? after.getName() : "-")
                .changeLog(changeLog)
                .build();

            changeDiffRepository.save(changeDiff);
        }

        if (!Objects.equals(before.getEmail(), after.getEmail())) {
            ChangeDiff changeDiff = ChangeDiff.builder()
                .propertyName("이메일")
                .oldValue(before.getEmail() != null ? before.getEmail() : "-")
                .newValue(after.getEmail() != null ? after.getEmail() : "-")
                .changeLog(changeLog)
                .build();

            changeDiffRepository.save(changeDiff);
        }

        if (!Objects.equals(before.getPosition(), after.getPosition())) {
            ChangeDiff changeDiff = ChangeDiff.builder()
                .propertyName("직함")
                .oldValue(before.getPosition() != null ? before.getPosition() : "-")
                .newValue(after.getPosition() != null ? after.getPosition() : "-")
                .changeLog(changeLog)
                .build();

            changeDiffRepository.save(changeDiff);
        }

        if (!Objects.equals(before.getHireDate(), after.getHireDate())) {
            ChangeDiff changeDiff = ChangeDiff.builder()
                .propertyName("입사일")
                .oldValue(before.getHireDate() != null ? before.getHireDate().toString() : "-")
                .newValue(after.getHireDate() != null ? after.getHireDate().toString() : "-")
                .changeLog(changeLog)
                .build();
            changeDiffRepository.save(changeDiff);
        }

        if (!Objects.equals(before.getStatus(), after.getStatus())) {
            ChangeDiff changeDiff = ChangeDiff.builder()
                .propertyName("상태")
                .oldValue(
                    before.getStatus() != null ? before.getStatus().getDescription() : "-")
                .newValue(after.getStatus() != null ? after.getStatus().getDescription() : "-")
                .changeLog(changeLog)
                .build();

            changeDiffRepository.save(changeDiff);
        }

        if (!Objects.equals(
            before.getDepartment() != null ? before.getDepartment().getId() : null,
            after.getDepartment() != null ? after.getDepartment().getId() : null
        )) {
            ChangeDiff changeDiff = ChangeDiff.builder()
                .propertyName("부서")
                .oldValue(
                    before.getDepartment() != null ? before.getDepartment().getName() : "-")
                .newValue(after.getDepartment() != null ? after.getDepartment().getName() : "-")
                .changeLog(changeLog)
                .build();

            changeDiffRepository.save(changeDiff);
        }

        if (!Objects.equals(
            before.getProfileImage() != null ? before.getProfileImage().getId() : null,
            after.getProfileImage() != null ? after.getProfileImage().getId() : null
        )) {
            ChangeDiff changeDiff = ChangeDiff.builder()
                .propertyName("프로필")
                .oldValue(
                    before.getProfileImage() != null ? before.getProfileImage().getName() : "-")
                .newValue(after.getPosition() != null ? after.getProfileImage().getName() : "-")
                .changeLog(changeLog)
                .build();

            changeDiffRepository.save(changeDiff);
        }
    }

    private String employeeNumberGenerator() {
        Long recentlyId = employeeRepository.findAll().stream().sorted(Comparator.comparing(Employee::getId).reversed())
                .findFirst().map(Employee::getId).orElse(0L);

        int year = LocalDate.now().getYear();

        return String.format("EMP-%d-%03d", year, recentlyId + 1);
    }
}
