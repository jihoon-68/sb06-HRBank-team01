package com.sprint.hrbank_sb6_1.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.hrbank_sb6_1.domain.Employee;
import com.sprint.hrbank_sb6_1.domain.EmployeeStatus;
import com.sprint.hrbank_sb6_1.domain.QEmployee;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponse;
import com.sprint.hrbank_sb6_1.dto.data.EmployeeTrendDto;
import com.sprint.hrbank_sb6_1.dto.request.EmployeeFindAllRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QEmployee employee = QEmployee.employee;

    @Override
    public CursorPageResponse<Employee> findAll(EmployeeFindAllRequest request) {
        OrderSpecifier<?> primarySort = getOrderSpecifier(request.getSortField(), request.getSortDirection());
        OrderSpecifier<Long> secondarySort = request.getSortDirection().equalsIgnoreCase("desc")
                ? employee.id.desc() : employee.id.asc();

        List<Employee> content = queryFactory.selectFrom(employee)
                .where(nameOrEmailContains(request.getNameOrEmail()), employeeNumberContains(request.getEmployeeNumber()),
                        departmentNameContains(request.getDepartmentName()), positionContains(request.getPosition()),
                        betweenHireDate(request.getHireDateFrom(), request.getHireDateTo()), statusEq(request.getStatus()),
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
                        betweenHireDate(request.getHireDateFrom(), request.getHireDateTo()), statusEq(request.getStatus()))
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
        StringTemplate dateFormat = switch (unit) {
            case "day" -> Expressions.stringTemplate(
                    "TO_CHAR({0}, 'YYYY-MM-DD')", employee.hireDate);
            case "week" -> Expressions.stringTemplate(
                    "TO_CHAR(DATE_TRUNC('week', {0}), 'YYYY-MM-DD')", employee.hireDate);
            case "quarter" -> Expressions.stringTemplate(
                    "TO_CHAR(DATE_TRUNC('quarter', {0}), 'YYYY-MM-DD')", employee.hireDate);
            case "year" -> Expressions.stringTemplate(
                    "TO_CHAR(DATE_TRUNC('year', {0}), 'YYYY-MM-DD')", employee.hireDate);
            default -> Expressions.stringTemplate(
                    "TO_CHAR(DATE_TRUNC('month', {0}), 'YYYY-MM-DD')", employee.hireDate);
        };

        return queryFactory
                .select(Projections.constructor(EmployeeTrendDto.class,
                        dateFormat.as("date"),
                        employee.count().as("count")
                ))
                .from(employee)
                .where(employee.hireDate.between(from, to))
                .groupBy(dateFormat)
                .orderBy(dateFormat.asc())
                .fetch();
    }

    @Override
    public Long getCount(EmployeeStatus status, String hireDateFrom, String hireDateTo) {
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

    private BooleanExpression betweenHireDate(String hireDateFrom, String hireDateTo) {
        if (hireDateFrom == null && hireDateTo == null) {
            return null;
        }

        LocalDate from = hireDateFrom != null ? LocalDate.parse(hireDateFrom) : null;
        LocalDate to = hireDateTo != null ? LocalDate.parse(hireDateTo) : null;

        if (hireDateFrom != null && hireDateTo != null) {
            return employee.hireDate.between(from, to);
        } else if (hireDateFrom != null) {
            return employee.hireDate.goe(from);
        } else {
            return employee.hireDate.loe(to);
        }
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
