package com.sprint.hrbank_sb6_1.repository;

import com.sprint.hrbank_sb6_1.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
  boolean existsByName(String name);
}
