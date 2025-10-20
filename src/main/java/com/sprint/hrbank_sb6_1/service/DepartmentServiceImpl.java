package com.sprint.hrbank_sb6_1.service;

import com.sprint.hrbank_sb6_1.domain.Department;
import com.sprint.hrbank_sb6_1.dto.DepartmentCreateRequest;
import com.sprint.hrbank_sb6_1.dto.DepartmentResponse;
import com.sprint.hrbank_sb6_1.dto.DepartmentSearchCond;
import com.sprint.hrbank_sb6_1.dto.DepartmentUpdateRequest;
import com.sprint.hrbank_sb6_1.dto.SortDirection;
import com.sprint.hrbank_sb6_1.repository.DepartmentRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

  private final DepartmentRepository departmentRepository;
//  private final EmployeeRepository employeeRepository;

  @Override
  public Page<DepartmentResponse> findAll(DepartmentSearchCond cond) {
    Pageable pageable = toPageable(cond);



    Page<Department> page = departmentRepository.search(cond.query(), pageable);
//    return page.map(d -> DepartmentResponse.of(d, employeeRepository.countByDepartmentId(d.getId())));
  }

  @Override
  @Transactional
  public DepartmentResponse create(DepartmentCreateRequest req) {
    if (departmentRepository.existsByName(req.name())) {
      throw new IllegalArgumentException("이미 존재하는 부서명입니다: " + req.name());
    }
    Department saved = departmentRepository.save(
        new Department(req.name(), req.description(), req.establishedDate())
    );
    return DepartmentResponse.of(saved);
  }

  @Override
  public DepartmentResponse findById(Integer id) {
    Department d = departmentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("부서를 찾을 수 없습니다: " + id));
    int count = employeeRepository.countByDepartmentId(d.getId());
    return DepartmentResponse.of(d, count);
  }

  @Override
  @Transactional
  public DepartmentResponse update(Integer id, DepartmentUpdateRequest req) {
    Department d = departmentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("부서를 찾을 수 없습니다: " + id));

    // 이름 변경 시 유니크 체크
    if (req.name() != null && !req.name().equals(d.getName())) {
      if (departmentRepository.existsByName(req.name())) {
        throw new IllegalArgumentException("이미 존재하는 부서명입니다: " + req.name());
      }
      d.setName(req.name());
    }
    if (req.description() != null) d.setDescription(req.description());
    if (req.establishedDate() != null) d.setEstablishedDate(req.establishedDate());

    // 더티체킹으로 자동 업데이트 (save 불필요)
    int count = employeeRepository.countByDepartmentId(d.getId());
    return DepartmentResponse.of(d, count);
  }

  @Override
  @Transactional
  public void delete(Integer id) {
    Department d = departmentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("부서를 찾을 수 없습니다: " + id));

    if (employeeRepository.existsByDepartmentId(id)) {
      throw new IllegalStateException("소속 직원이 있는 부서는 삭제할 수 없습니다.");
    }
    departmentRepository.delete(d);
  }

  // 정렬/페이지  변환
  private Pageable toPageable(DepartmentSearchCond cond) {
    Sort sort = switch (cond.sortBy()) {
      case NAME -> Sort.by(cond.direction() == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, "name").and(Sort.by("id")); // 안정 정렬
      case ESTABLISHED_DATE -> Sort.by(cond.direction() == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, "establishedDate").and(Sort.by("id"));
    };
    return PageRequest.of(Math.max(cond.page(), 0), Math.max(cond.size(), 1), sort);
  }
}