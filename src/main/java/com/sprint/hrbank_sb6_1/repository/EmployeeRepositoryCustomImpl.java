package com.sprint.hrbank_sb6_1.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.hrbank_sb6_1.domain.Employee;
import com.sprint.hrbank_sb6_1.domain.EmployeeStatus;
import com.sprint.hrbank_sb6_1.domain.QEmployee;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponse;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeTrendDto;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeFindAllRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    private final QEmployee employee = QEmployee.employee;

    @Override
    public CursorPageResponse<Employee> findAll(EmployeeFindAllRequest request) {
        OrderSpecifier<?> primarySort = getOrderSpecifier(request.getSortField(), request.getSortDirection());
        OrderSpecifier<Long> secondarySort = request.getSortDirection().equalsIgnoreCase("desc")
                ? employee.id.desc() : employee.id.asc();

        LocalDate from = request.getHireDateFrom() != null ? LocalDate.parse(request.getHireDateFrom()) : null;
        LocalDate to = request.getHireDateTo() != null ? LocalDate.parse(request.getHireDateTo()) : LocalDate.now();

        List<Employee> content = queryFactory.selectFrom(employee)
                .where(nameOrEmailContains(request.getNameOrEmail()), employeeNumberContains(request.getEmployeeNumber()),
                        departmentNameContains(request.getDepartmentName()), positionContains(request.getPosition()),
                        betweenHireDate(from, to), statusEq(request.getStatus()),
                        cursorIdAfter(request.getCursor(), request.getIdAfter(), request.getSortField(), request.getSortDirection()))
                .orderBy(primarySort, secondarySort)
                .limit(request.getSize() + 1)
                .fetch();

        boolean hasNext = content.size() > request.getSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }

        String nextCursor = null;
        Long nextIdAfter = null;

        if (!content.isEmpty()) {
            nextCursor = switch (request.getSortField()) {
                case "employeeNumber" -> content.get(content.size() - 1).getEmployeeNumber();
                case "hireDate" -> content.get(content.size() - 1).getHireDate().toString();
                default -> content.get(content.size() - 1).getName();
            };

            nextIdAfter = content.get(content.size() - 1).getId();
        }

        Long total = queryFactory
                .select(employee.count())
                .from(employee)
                .where(nameOrEmailContains(request.getNameOrEmail()), employeeNumberContains(request.getEmployeeNumber()),
                        departmentNameContains(request.getDepartmentName()), positionContains(request.getPosition()),
                        betweenHireDate(from, to), statusEq(request.getStatus()))
                .fetchOne();

        return CursorPageResponse.<Employee>builder()
                .nextIdAfter(nextIdAfter)
                .content(content)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .totalElements(total != null ? total : 0L)
                .size(content.size())
                .build();
    }

    @Override
    public List<EmployeeTrendDto> getTrend(LocalDate from, LocalDate to, String unit) {
        String dateTrunc = switch (unit) {
            case "day" -> "day";
            case "week" -> "week";
            case "quarter" -> "quarter";
            case "year" -> "year";
            default -> "month";
        };

        String periodEnd = switch (unit) {
            case "day" -> "ds.period_date";
            case "week" -> "ds.period_date + INTERVAL '6 days'";
            case "quarter" -> "ds.period_date + INTERVAL '3 months' - INTERVAL '1 day'";
            case "year" -> "ds.period_date + INTERVAL '1 year' - INTERVAL '1 day'";
            default -> "ds.period_date + INTERVAL '1 month' - INTERVAL '1 day'";
        };

        String intervalIncrement = switch (unit) {
            case "day" -> "INTERVAL '1 day'";
            case "week" -> "INTERVAL '1 week'";
            case "quarter" -> "INTERVAL '3 months'";
            case "year" -> "INTERVAL '1 year'";
            default -> "INTERVAL '1 month'";
        };

        String sql = String.format("""
            WITH RECURSIVE date_series AS (
                SELECT DATE_TRUNC(:trunc, CAST(:from AS date)) as period_date
                UNION ALL
                SELECT period_date + %s
                FROM date_series
                WHERE period_date < DATE_TRUNC(:trunc, CAST(:to AS date))
            )
            SELECT 
                TO_CHAR(ds.period_date, 'YYYY-MM-DD') as date,
                COUNT(e.id) as count
            FROM date_series ds
            LEFT JOIN employee e ON e.hire_date <= %s
            GROUP BY ds.period_date
            ORDER BY ds.period_date
            """, intervalIncrement, periodEnd);

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("trunc", dateTrunc);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(row -> new EmployeeTrendDto(
                        (String) row[0],           // date
                        ((Number) row[1]).longValue()  // count
                ))
                .collect(Collectors.toList());
    }


    @Override
    public Long getCount(EmployeeStatus status, LocalDate hireDateFrom, LocalDate hireDateTo) {
        return queryFactory.select(employee.count())
                .from(employee)
                .where(statusEq(status), betweenHireDate(hireDateFrom, hireDateTo))
                .fetchOne();
    }

    private BooleanExpression nameOrEmailContains(String nameOrEmail) {
        return nameOrEmail != null ? employee.name.contains(nameOrEmail).or(QEmployee.employee.email.contains(nameOrEmail)) : null;
    }

    private BooleanExpression employeeNumberContains(String employeeNumber) {
        return employeeNumber != null ? employee.employeeNumber.contains(employeeNumber) : null;
    }

    private BooleanExpression departmentNameContains(String departmentName) {
        return departmentName != null ? employee.department.name.contains(departmentName) : null;
    }

    private BooleanExpression positionContains(String position) {
        return position != null ? employee.position.contains(position) : null;
    }

    private BooleanExpression betweenHireDate(LocalDate hireDateFrom, LocalDate hireDateTo) {
        if (hireDateFrom == null && hireDateTo == null) {
            return null;
        }

        if (hireDateFrom != null && hireDateTo != null) {
            return employee.hireDate.between(hireDateFrom, hireDateTo);
        }

        if (hireDateFrom != null) {
            return employee.hireDate.goe(hireDateFrom);
        }

        return employee.hireDate.loe(hireDateTo);
    }

    private BooleanExpression statusEq(EmployeeStatus status) {
        return status != null ? employee.status.eq(status) : null;
    }

    private BooleanExpression cursorIdAfter(String cursor, Long idAfter, String sortField, String sortDirection) {
        if (cursor == null || idAfter == null) {
            return null;
        }

        boolean isDesc = sortDirection.equalsIgnoreCase("desc");

        return switch (sortField) {
            case "employeeNumber" -> {
                if (isDesc) {
                    yield employee.employeeNumber.lt(cursor)
                            .or(employee.employeeNumber.eq(cursor).and(employee.id.lt(idAfter)));
                } else {
                    yield employee.employeeNumber.gt(cursor)
                            .or(employee.employeeNumber.eq(cursor).and(employee.id.gt(idAfter)));
                }
            }
            case "hireDate" -> {
                LocalDate date = LocalDate.parse(cursor);
                if (isDesc) {
                    yield employee.hireDate.lt(date)
                            .or(employee.hireDate.eq(date).and(employee.id.lt(idAfter)));
                } else {
                    yield employee.hireDate.gt(date)
                            .or(employee.hireDate.eq(date).and(employee.id.gt(idAfter)));
                }
            }
            default -> {
                if (isDesc) {
                    yield employee.name.lt(cursor)
                            .or(employee.name.eq(cursor).and(employee.id.lt(idAfter)));
                } else {
                    yield employee.name.gt(cursor)
                            .or(employee.name.eq(cursor).and(employee.id.gt(idAfter)));
                }
            }
        };
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortField, String sortDirection) {
        return switch (sortField) {
            case "employeeNumber" ->
                    sortDirection.equalsIgnoreCase("desc") ? employee.employeeNumber.desc() : employee.employeeNumber.asc();
            case "hireDate" ->
                    sortDirection.equalsIgnoreCase("desc") ? employee.hireDate.desc() : employee.hireDate.asc();
            default -> sortDirection.equalsIgnoreCase("desc") ? employee.name.desc() : employee.name.asc();
        };
    }
}
