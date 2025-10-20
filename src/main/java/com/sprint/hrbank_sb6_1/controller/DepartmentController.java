package com.sprint.hrbank_sb6_1.controller;

import com.sprint.hrbank_sb6_1.domain.Department;
import com.sprint.hrbank_sb6_1.dto.DepartmentCreateRequest;
import com.sprint.hrbank_sb6_1.dto.DepartmentResponse;
import com.sprint.hrbank_sb6_1.service.DepartmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DepartmentController {

  private final DepartmentService departmentService;

  @PostMapping(path="/departments")
  public DepartmentResponse create(@RequestBody DepartmentCreateRequest dto) {
    return departmentService.create(dto);
  }

  @GetMapping(path = "/departments")
  public List<DepartmentResponse> findAll() {
    return departmentService.findAll();
  }

  @GetMapping("/departments/{id}")
  public Department get(@PathVariable Integer id) {
    return departmentService.findById(id)
  }

  @DeleteMapping("/departments/{id}")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    departmentRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/departments/{id}")
  public ResponseEntity<Void> update(@PathVariable Integer id) {
    departmentRepository.

  }
}
