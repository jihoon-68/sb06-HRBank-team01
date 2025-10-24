package com.sprint.hrbank_sb6_1.backup.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.hrbank_sb6_1.backup.domain.QScheduledBackup;
import com.sprint.hrbank_sb6_1.backup.dto.ScheduledBackupResponseDto;
import com.sprint.hrbank_sb6_1.backup.dto.ScheduledBackupSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduledBackupRepositoryImpl implements ScheduledBackupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ScheduledBackupResponseDto> search(ScheduledBackupSearchCondition c) {
        QScheduledBackup sb = QScheduledBackup.scheduledBackup;

        int size = (c.getSize() != null && c.getSize() > 0) ? c.getSize() : 10;
        String sortField = StringUtils.hasText(c.getSortField()) ? c.getSortField() : "startedAt";
        String sortDir = StringUtils.hasText(c.getSortDirection()) ? c.getSortDirection().toUpperCase() : "DESC";

        // WHERE 동적 조건
        List<BooleanExpression> where = new ArrayList<>();
        if (StringUtils.hasText(c.getWorker())) {
            where.add(sb.worker.containsIgnoreCase(c.getWorker()));
        }
        if (c.getStartedAtFrom() != null) {
            where.add(sb.startedAt.goe(c.getStartedAtFrom()));
        }
        if (c.getStartedAtTo() != null) {
            where.add(sb.startedAt.loe(c.getStartedAtTo()));
        }
        if (c.getStatus() != null) {
            where.add(sb.status.eq(c.getStatus()));
        }
        // 커서(마지막 ID 기준) – 정렬방향과 관계없이 tiebreaker 로 id 사용
        if (c.getLastId() != null) {
            if ("ASC".equalsIgnoreCase(sortDir)) {
                where.add(sb.id.gt(c.getLastId()));
            } else {
                where.add(sb.id.lt(c.getLastId()));
            }
        }

        // 정렬 지정 (선택 필드 + id 보조정렬)
        OrderSpecifier<?> primaryOrder = toOrderSpecifier(sb, sortField, sortDir);
        OrderSpecifier<?> tieBreaker = "ASC".equalsIgnoreCase(sortDir) ? sb.id.asc() : sb.id.desc();

        // fileCount: backupFile != null ? 1 : 0
        var fileCountExpr = Expressions.cases()
                .when(sb.backupFile.isNotNull()).then(1)
                .otherwise(0);

        return queryFactory
                .select(Projections.fields(
                        ScheduledBackupResponseDto.class,
                        sb.id.as("id"),
                        sb.worker.as("worker"),
                        sb.startedAt.as("startedAt"),
                        sb.endedAt.as("endedAt"),
                        sb.status.as("status"),
                        fileCountExpr.as("fileCount")
                ))
                .from(sb)
                .where(andAll(where))
                .orderBy(primaryOrder, tieBreaker)
                .limit(size)
                .fetch();
    }

    private BooleanExpression andAll(List<BooleanExpression> exps) {
        BooleanExpression result = null;
        for (BooleanExpression e : exps) {
            result = (result == null) ? e : result.and(e);
        }
        return result;
    }

    private OrderSpecifier<?> toOrderSpecifier(QScheduledBackup sb, String field, String dir) {
        Order order = "ASC".equalsIgnoreCase(dir) ? Order.ASC : Order.DESC;
        return switch (field) {
            case "endedAt" -> new OrderSpecifier<>(order, sb.endedAt);
            case "status"  -> new OrderSpecifier<>(order, sb.status);
            default        -> new OrderSpecifier<>(order, sb.startedAt); // 기본 startedAt
        };
    }
}