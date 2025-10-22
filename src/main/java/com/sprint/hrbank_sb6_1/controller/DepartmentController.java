package com.sprint.hrbank_sb6_1.controller;

import com.sprint.hrbank_sb6_1.dto.CursorPagedResponse;
import com.sprint.hrbank_sb6_1.dto.DepartmentCreateRequest;
import com.sprint.hrbank_sb6_1.dto.DepartmentResponse;
import com.sprint.hrbank_sb6_1.dto.DepartmentSearchCond;
import com.sprint.hrbank_sb6_1.dto.DepartmentSortBy;
import com.sprint.hrbank_sb6_1.dto.DepartmentUpdateRequest;
import com.sprint.hrbank_sb6_1.dto.SortDirection;
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

  @GetMapping("/departments")
  public CursorPagedResponse<DepartmentResponse> findDepartments(
      @RequestParam(required = false) String nameOrDescription,
      @RequestParam(required = false) Long idAfter,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "ESTABLISHED_DATE") DepartmentSortBy sortField,
      @RequestParam(defaultValue = "ASC") SortDirection sortDirection
  ) {
    DepartmentSearchCond cond = new DepartmentSearchCond(
        nameOrDescription, idAfter, cursor, size, sortField, sortDirection
    );
    return departmentService.findAll(cond);
  }

  @GetMapping("/departments/{id}")
  public DepartmentResponse get(@PathVariable Long id) {
    return departmentService.findById(id);
  }

  @DeleteMapping("/departments/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    departmentService.delete(id);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/departments/{id}")
  public ResponseEntity<DepartmentResponse> update(@PathVariable Long id, @RequestBody DepartmentUpdateRequest dto) {
    var res = departmentService.update(id, dto);

    return ResponseEntity.ok().body(res);
  }
}
