package com.sprint.hrbank_sb6_1.repository;

import com.sprint.hrbank_sb6_1.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {
    boolean existsByEmail(String email);
}
