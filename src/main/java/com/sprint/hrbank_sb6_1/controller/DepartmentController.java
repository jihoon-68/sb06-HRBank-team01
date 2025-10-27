package com.sprint.hrbank_sb6_1.controller;

import com.sprint.hrbank_sb6_1.dto.CursorPageResponse;
import com.sprint.hrbank_sb6_1.dto.DepartmentCreateRequest;
import com.sprint.hrbank_sb6_1.dto.DepartmentResponse;
import com.sprint.hrbank_sb6_1.dto.DepartmentSearchCond;
import com.sprint.hrbank_sb6_1.dto.DepartmentSortBy;
import com.sprint.hrbank_sb6_1.dto.DepartmentUpdateRequest;
import com.sprint.hrbank_sb6_1.dto.SortDirection;
import com.sprint.hrbank_sb6_1.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DepartmentController {

  private final DepartmentService departmentService;

  @PostMapping(path = "/departments")
  public ResponseEntity<DepartmentResponse> create(@RequestBody DepartmentCreateRequest req) {
    var created = departmentService.create(req);

    return ResponseEntity
        .status(HttpStatus.CREATED) // 201 상태코드
        .body(created);
  }

  @GetMapping("/departments")
  public ResponseEntity<CursorPageResponse<DepartmentResponse>> findDepartments(
      @RequestParam(required = false) String nameOrDescription,
      @RequestParam(required = false) Long idAfter,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "establishedDate") DepartmentSortBy sortField,
      @RequestParam(defaultValue = "asc") SortDirection sortDirection
  ) {
    DepartmentSearchCond cond = new DepartmentSearchCond(
        nameOrDescription, idAfter, cursor, size, sortField, sortDirection
    );
    var res = departmentService.findAll(cond);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(res);
  }

  @GetMapping("/departments/{id}")
  public ResponseEntity<DepartmentResponse> get(@PathVariable Long id) {
    var res = departmentService.findById(id);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(res);
  }

  @DeleteMapping("/departments/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    departmentService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/departments/{id}")
  public ResponseEntity<DepartmentResponse> update(@PathVariable Long id,
      @RequestBody DepartmentUpdateRequest dto) {
    var res = departmentService.update(id, dto);

    return ResponseEntity.ok().body(res);
  }
}
