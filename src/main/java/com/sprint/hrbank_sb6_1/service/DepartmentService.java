package com.sprint.hrbank_sb6_1.service;

import com.sprint.hrbank_sb6_1.dto.DepartmentCreateRequest;
import com.sprint.hrbank_sb6_1.dto.DepartmentResponse;
import com.sprint.hrbank_sb6_1.dto.DepartmentSearchCond;
import com.sprint.hrbank_sb6_1.dto.DepartmentUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface DepartmentService {
  // 목록 조회 (검색/정렬/페이지네이션)
  Page<DepartmentResponse> findAll(DepartmentSearchCond cond);

  // 등록
  DepartmentResponse create(DepartmentCreateRequest req);

  // 상세
  DepartmentResponse findById(Integer id);

  // 부분수정 (PATCH)
  DepartmentResponse update(Integer id, DepartmentUpdateRequest req);

  // 삭제 (직원 없을 때만)
  void delete(Integer id);
}