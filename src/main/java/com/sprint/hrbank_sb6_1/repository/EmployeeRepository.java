package com.sprint.hrbank_sb6_1.repository;

import com.sprint.hrbank_sb6_1.domain.Employee;
import com.sprint.hrbank_sb6_1.domain.EmployeeStatus;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeDistributionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {
    boolean existsByEmail(String email);

    @Query("SELECT e FROM Employee e")
    Stream<Employee> streamAll();

    @Query("SELECT new com.sprint.hrbank_sb6_1.dto.data.EmployeeDistributionDto(e.department.name, COUNT(*)) " +
            "FROM Employee e " +
            "WHERE e.status = :status " +
            "GROUP BY e.department.name")
    List<EmployeeDistributionDto> findDistributionByStatusGroupByDepartment(@Param("status") EmployeeStatus status);

    @Query("SELECT new com.sprint.hrbank_sb6_1.dto.data.EmployeeDistributionDto(e.position, COUNT(*)) " +
            "FROM Employee e " +
            "WHERE e.status = :status " +
            "GROUP BY e.position")
    List<EmployeeDistributionDto> findDistributionByStatusGroupByPosition(@Param("status") EmployeeStatus status);
}