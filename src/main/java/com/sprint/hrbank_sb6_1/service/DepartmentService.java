package com.sprint.hrbank_sb6_1.service;

import com.sprint.hrbank_sb6_1.domain.Department;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponse;
import com.sprint.hrbank_sb6_1.dto.DepartmentCreateRequest;
import com.sprint.hrbank_sb6_1.dto.DepartmentResponse;
import com.sprint.hrbank_sb6_1.dto.DepartmentSearchCond;
import com.sprint.hrbank_sb6_1.dto.DepartmentSortBy;
import com.sprint.hrbank_sb6_1.dto.DepartmentUpdateRequest;
import com.sprint.hrbank_sb6_1.dto.SortDirection;
import com.sprint.hrbank_sb6_1.repository.DepartmentRepository;
import com.sprint.hrbank_sb6_1.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;

  @Transactional
  public DepartmentResponse create(DepartmentCreateRequest req) {
    if (departmentRepository.existsByName(req.name())) {
      throw new IllegalArgumentException("이미 존재하는 부서명입니다: " + req.name());
    }
    Department saved = departmentRepository.save(
        new Department(req.name(), req.description(), req.establishedDate())
    );
    return DepartmentResponse.from(saved);
  }

  public DepartmentResponse findById(Long id) {
    Department d = getDepartment(id);
    var employeeCount = employeeRepository.countByDepartmentId(d.getId());
    return DepartmentResponse.of(d, employeeCount);
  }

  @Transactional
  public DepartmentResponse update(Long id, DepartmentUpdateRequest req) {
    Department d = getDepartment(id);

    if (req.name() != null && !req.name().equals(d.getName())) {
      if (departmentRepository.existsByName(req.name())) {
        throw new IllegalArgumentException("이미 존재하는 부서명입니다: " + req.name());
      }
    }

    d.update(req);

    var employeeCount = employeeRepository.countByDepartmentId(d.getId());
    return DepartmentResponse.of(d, employeeCount);
  }

  @Transactional
  public void delete(Long id) {
    Department d = getDepartment(id);
    var employeeCount = employeeRepository.countByDepartmentId(d.getId());

    if (employeeCount > 0) {
      throw new IllegalStateException("소속 직원이 있는 부서는 삭제할 수 없습니다.");
    }
    departmentRepository.delete(d);
  }

  public CursorPageResponse<DepartmentResponse> findAll(DepartmentSearchCond condition) {

    // 데이터를 가져온다.
    Page<Department> rows = getDepartments(condition);

    String nextCursor = null;
    Long nextIdAfter = null;

    if (rows.hasNext()) {
      List<Department> list = rows.getContent();
      Department last = list.get(list.size() - 1);
      nextCursor = last.getEstablishedDate().toString(); // "yyyy-MM-dd"
      nextIdAfter = last.getId();
    }

    List<DepartmentResponse> content = rows.getContent().stream()
        .map(d -> {
          var count = employeeRepository.countByDepartmentId(d.getId());
          return DepartmentResponse.of((Department) d, count);
        })
        .toList();

    return CursorPageResponse.of(
        content,
        nextCursor,
        nextIdAfter,
        rows.getContent().size(),
        rows.getTotalElements(),
        rows.hasNext()
    );
  }

  private Page<Department> getDepartments(DepartmentSearchCond cond) {

    String convertedNameOrDescription = convertNameOrDescription(cond);
    LocalDate dateKey = convertedDateKey(cond);
    Long idKey = convertedIdKey(cond);

    if (cond.sortField() == DepartmentSortBy.name) {
      if (isAsc(cond)) {
        return departmentRepository.findNextByNameAsc(convertedNameOrDescription, cond.cursor(),
            idKey, Pageable.ofSize(cond.size()));
      }
      return departmentRepository.findNextByNameDesc(convertedNameOrDescription, cond.cursor(),
          idKey, Pageable.ofSize(cond.size()));
    }

    if (isAsc(cond)) {
      return departmentRepository.findNextByDateAsc(convertedNameOrDescription, dateKey, idKey,
          Pageable.ofSize(cond.size()));
    }

    return departmentRepository.findNextByDateDesc(convertedNameOrDescription, dateKey, idKey,
        Pageable.ofSize(cond.size()));
  }

  private Department getDepartment(Long id) {
    return departmentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("부서를 찾을 수 없습니다: " + id));
  }

  private boolean isAsc(DepartmentSearchCond cond) {
    return cond.sortDirection() == null || cond.sortDirection() == SortDirection.asc;
  }

  private LocalDate convertedDateKey(DepartmentSearchCond cond) {
    if (cond.cursor() == null) {
      return null;
    }
    if (cond.cursor().isBlank()) {
      return null;
    }
    return LocalDate.parse(cond.cursor());
  }

  private Long convertedIdKey(DepartmentSearchCond cond) {
    if (cond.idAfter() == null) {
      return null;
    }
    return cond.idAfter();
  }

  private String convertNameOrDescription(DepartmentSearchCond cond) {
    if (cond.nameOrDescription() == null) {
      return null;
    }
    if (cond.nameOrDescription().isBlank()) {
      return null;
    }
    return "%" + cond.nameOrDescription().toLowerCase(Locale.ROOT).trim() + "%";
  }
}