package com.sprint.hrbank_sb6_1.repository;

import com.sprint.hrbank_sb6_1.domain.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

  boolean existsByName(String name);

  @Query("""
  SELECT d FROM Department d
  WHERE (:q IS NULL
         OR LOWER(d.name) LIKE :q
         OR LOWER(d.description) LIKE :q)
    AND (
         :afterDate IS NULL
      OR d.establishedDate > :afterDate
      OR (d.establishedDate = :afterDate AND d.id > :idAfter)
    )
  ORDER BY d.establishedDate ASC, d.id ASC
""")
  Page<Department> findNextByDateAsc(
      @Param("q") String q,
      @Param("afterDate") LocalDate afterDate,
      @Param("idAfter") Long idAfter,
      Pageable pageable
  );

  // 이름 + 설명 검색 (DATE 내림차순)
  @Query("""
      SELECT d FROM Department d
      WHERE (:q IS NULL OR d.name LIKE %:q% OR d.description LIKE %:q%)
        AND (
            (:afterDate IS NULL)
            OR (d.establishedDate < :afterDate)
            OR (d.establishedDate = :afterDate AND d.id < :idAfter)
      )
      ORDER BY d.establishedDate DESC, d.id DESC
    """)
  Page<Department> findNextByDateDesc(
      @Param("q") String q,
      @Param("afterDate") LocalDate afterDate,
      @Param("idAfter") Long idAfter,
      Pageable pageable
  );

  // 이름 기준 오름차순
  @Query("""
        SELECT d FROM Department d
        WHERE (:q IS NULL OR d.name LIKE %:q% OR d.description LIKE %:q%)
          AND (
              (:afterName IS NULL)
              OR (d.name > :afterName)
              OR (d.name = :afterName AND d.id > :idAfter)
          )
        ORDER BY d.name ASC, d.id ASC
    """)
  Page<Department> findNextByNameAsc(
      @Param("q") String q,
      @Param("afterName") String afterName,
      @Param("idAfter") Long idAfter,
      Pageable pageable
  );

  // 이름 기준 내림차순
  @Query("""
        SELECT d FROM Department d
        WHERE (:q IS NULL OR d.name LIKE %:q% OR d.description LIKE %:q%)
          AND (
              (:afterName IS NULL)
              OR (d.name < :afterName)
              OR (d.name = :afterName AND d.id < :idAfter)
          )
        ORDER BY d.name DESC, d.id DESC
    """)
  Page<Department> findNextByNameDesc(
      @Param("q") String q,
      @Param("afterName") String afterName,
      @Param("idAfter") Long idAfter,
      Pageable pageable
  );

  @Query("""
        SELECT COUNT(d) FROM Department d
        WHERE (:q IS NULL OR d.name LIKE %:q% OR d.description LIKE %:q%)
    """)
  long countByQuery(@Param("q") String q);
}