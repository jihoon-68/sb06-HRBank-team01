package com.sprint.hrbank_sb6_1.service;

import com.sprint.hrbank_sb6_1.domain.Department;
import com.sprint.hrbank_sb6_1.dto.CursorPagedResponse;
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
public class DepartmentServiceImpl {

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
    return DepartmentResponse.of(saved, 0);
  }

  public DepartmentResponse findById(Integer id) {
    Department d = departmentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("부서를 찾을 수 없습니다: " + id));
    // FIXME: employee가 완성된 후 인원 수 추가하기
    // int count = employeeRepository.countByDepartmentId(d.getId());
    return DepartmentResponse.of(d, 10);
  }

  @Transactional
  public DepartmentResponse update(Integer id, DepartmentUpdateRequest req) {
    Department d = departmentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("부서를 찾을 수 없습니다: " + id));

    if (req.name() != null && !req.name().equals(d.getName())) {
      if (departmentRepository.existsByName(req.name())) {
        throw new IllegalArgumentException("이미 존재하는 부서명입니다: " + req.name());
      }
      d.setName(req.name());
    }
    if (req.description() != null) {
      d.setDescription(req.description());
    }
    if (req.establishedDate() != null) {
      d.setEstablishedDate(req.establishedDate());
    }

    // FIXME: employee가 완성된 후 인원 수 추가하기
    // int count = employeeRepository.countByDepartmentId(d.getId());
    return DepartmentResponse.of(d, 10);
  }

  @Transactional
  public void delete(Integer id) {
    Department d = departmentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("부서를 찾을 수 없습니다: " + id));
    // FIXME: 부서의 아이디를 기반으로 유저가 있는지 혹은 다른 메소드로 판단하여 제거여부 확인
//    if (employeeRepository.existsByDepartmentId(id)) {
//      throw new IllegalStateException("소속 직원이 있는 부서는 삭제할 수 없습니다.");
//    }
    departmentRepository.delete(d);
  }

  public CursorPagedResponse<DepartmentResponse> findAll(DepartmentSearchCond condition) {

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
        .map(d -> DepartmentResponse.of((Department) d, 0))
        .toList();

    // 최종 반환 (요청한 CursorPagedResponse.of 사용)
    return CursorPagedResponse.of(
        content,
        nextCursor,
        nextIdAfter,
        rows.getContent().size(),
        rows.getTotalElements(),        // 커서 방식에서는 보통 totalElements 생략(원하면 별도 count 쿼리)
        rows.hasNext()
    );
  }

  private Page<Department> getDepartments(DepartmentSearchCond cond) {

    String convertedNameOrDescription = convertNameOrDescription(cond);
    LocalDate dateKey = convertedDateKey(cond);
    Long idKey = convertedIdKey(cond);

    if (cond.sortField() == DepartmentSortBy.NAME) {
      if (isAsc(cond)) {
        return departmentRepository.findNextByDateAsc(convertedNameOrDescription, dateKey, idKey, Pageable.ofSize(cond.size()));
      }
      return departmentRepository.findNextByDateDesc(convertedNameOrDescription, dateKey, idKey, Pageable.ofSize(cond.size()));
    }

    if (isAsc(cond)) {
      return departmentRepository.findNextByDateAsc(convertedNameOrDescription, dateKey, idKey, Pageable.ofSize(cond.size()));
    }

    return departmentRepository.findNextByDateDesc(convertedNameOrDescription, dateKey, idKey, Pageable.ofSize(cond.size()));
  }

  private boolean isAsc(DepartmentSearchCond cond) {
    return cond.sortDirection() == null || cond.sortDirection() == SortDirection.ASC;
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
    return cond.idAfter().longValue();
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