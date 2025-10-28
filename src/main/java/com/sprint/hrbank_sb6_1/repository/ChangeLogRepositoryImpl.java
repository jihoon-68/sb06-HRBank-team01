package com.sprint.hrbank_sb6_1.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.hrbank_sb6_1.domain.ChangeLog;
import com.sprint.hrbank_sb6_1.domain.ChangeLogStatus;
import com.sprint.hrbank_sb6_1.domain.QChangeLog;
import com.sprint.hrbank_sb6_1.domain.QEmployee;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChangeLogRepositoryImpl implements ChangeLogRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChangeLog> searchChangeLogs(String employeeNumber, int type,
        String memo, String ipAddress, LocalDateTime atFrom, LocalDateTime atTo, Long idAfter,
        Pageable pageable) {
        QChangeLog c = QChangeLog.changeLog;
        QEmployee e = QEmployee.employee;


        BooleanBuilder builder = new BooleanBuilder();

        // employeeNumber (부분 일치)
        if (employeeNumber != null && !employeeNumber.isEmpty()) {
            builder.and(c.employee.employeeNumber.like("%" + employeeNumber + "%"));
        }

        // status (정확 일치)
        if (type != 0) {
            builder.and(c.status.eq(type));
        }

        // memo (부분 일치)
        if (memo != null && !memo.isEmpty()) {
            builder.and(c.memo.like("%" + memo + "%"));
        }

        // ipAddress (부분 일치)
        if (ipAddress != null && !ipAddress.isEmpty()) {
            builder.and(c.address.like("%" + ipAddress + "%"));
        }

        // atFrom ~ atTo (날짜 범위)
        if (atFrom != null) {
            builder.and(c.at.goe(atFrom)); // greater or equal
        }
        if (atTo != null) {
            builder.and(c.at.loe(atTo)); // less or equal
        }

        // idAfter (커서 페이징)
        if (idAfter != null) {
            builder.and(c.id.gt(idAfter));
        }

        // 정렬: at DESC
        OrderSpecifier<?> orderSpecifier = c.at.desc();

        // Query 실행
        return queryFactory
            .selectFrom(c)
            .leftJoin(c.employee, e).fetchJoin()
            .where(builder)
            .orderBy(orderSpecifier)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Long countByAtBetween(LocalDateTime fromDate, LocalDateTime toDate) {
        QChangeLog c = QChangeLog.changeLog;

        // 기본값 설정
        LocalDateTime effectiveFrom = (fromDate != null) ? fromDate : LocalDateTime.now().minusDays(7);
        LocalDateTime effectiveTo = (toDate != null) ? toDate : LocalDateTime.now();

        return queryFactory
            .select(c.count())
            .from(c)
            .where(c.at.between(effectiveFrom, effectiveTo))
            .fetchOne();
    }
}
