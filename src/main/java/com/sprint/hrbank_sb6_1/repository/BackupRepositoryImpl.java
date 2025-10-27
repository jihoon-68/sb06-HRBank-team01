package com.sprint.hrbank_sb6_1.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.hrbank_sb6_1.domain.Backup;
import com.sprint.hrbank_sb6_1.domain.BackupStatus;
import com.sprint.hrbank_sb6_1.dto.request.SearchBackupRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

import static com.sprint.hrbank_sb6_1.domain.QBackup.backup;

@AllArgsConstructor
public class BackupRepositoryImpl implements BackupRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("startedAt", "endedAt", "status");

    @Override
    public Slice<Backup> searchTasks(SearchBackupRequest search) {

        int requestSize = search.getSize();

        List<Backup> content = queryFactory
                .selectFrom(backup)
                .where(
                        workerContains(search),

                        cursorCondition(search)
                ).orderBy(
                        dynamicOrderBy(search)
                )
                .limit(requestSize+1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > requestSize) {
            hasNext = true;
            content.remove(requestSize);
        }

        Pageable pageable = PageRequest.of(0, requestSize);
        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public long countTasks(SearchBackupRequest search) {
        Long count = queryFactory
                .select(backup.count())
                .from(backup)
                .where(
                        workerContains(search)
                )
                .fetchOne();
        return (count != null) ? count : 0L;
    }

    private BooleanExpression workerContains(SearchBackupRequest search) {
        return backup.isNotNull()
                .and(workerContains(search.getWorker()))
                .and(statusEquals(search.getStatus()))
                .and(startedAtGoe(search.getStartedAtFrom()))
                .and(startedAtLoe(search.getStartedAtTo()));
    }

    //사용자 부분일치
    private BooleanExpression workerContains(String worker) {
        return StringUtils.hasText(worker) ? backup.worker.contains(worker) : null;
    }

    //상태 완전일치
    private BooleanExpression statusEquals(BackupStatus status) {
        if (status == null) {return null;}
        return StringUtils.hasText(String.valueOf(status)) ? backup.status.eq(status) : null;
    }

    //백업 시작 날짜 부터 범위일치
    private BooleanExpression startedAtGoe(LocalDateTime from) {
        return (from != null) ? backup.startedAt.goe(from) : null;
    }

    //백업 시작 날짜 까지 범위일치
    private BooleanExpression startedAtLoe(LocalDateTime to) {
        return (to != null) ? backup.startedAt.loe(to) : null;
    }

    //커서 WHERE 절 로작
    private BooleanExpression cursorCondition(SearchBackupRequest  search) {
        if(!StringUtils.hasText(search.getCursor())|| !"startAt".equals(search.getCursor())) {
            return null;
        }

        LocalDateTime cursorValue = LocalDateTime.parse(search.getCursor());

        if("DESC".equalsIgnoreCase(search.getSortDirection())){
            return backup.startedAt.lt(cursorValue); // 내림차순
        }else {
            return backup.startedAt.gt(cursorValue); // 오름차순
        }
    }

    //동적 ORDER BY 로직
    private OrderSpecifier<?>[] dynamicOrderBy(SearchBackupRequest  search) {
        String sortField = search.getSortField();
        Order direction = "ASC".equalsIgnoreCase(search.getSortDirection()) ? Order.ASC : Order.DESC;

        PathBuilder<Backup> entityPath = new PathBuilder<>(Backup.class, "backup");
        Expression<? extends Comparable> primaryExpression;

        if("startedAt".equals(sortField)) {
            primaryExpression = entityPath.getDateTime("startedAt", LocalDateTime.class);
        }else if("endedAt".equals(sortField)) {
            primaryExpression = entityPath.getDateTime("endedAt", LocalDateTime.class);
        }else if("status".equals(sortField)) {
            primaryExpression = entityPath.getString("status");
        } else {
            sortField = "id";
            primaryExpression = entityPath.getDateTime("id", Long.class);
            return new OrderSpecifier[]{ new OrderSpecifier<>(direction, primaryExpression) };
        }

        OrderSpecifier<?> primaryOrder = new OrderSpecifier<>(direction,primaryExpression);

        OrderSpecifier<?> secondaryOrder = new OrderSpecifier<>(direction, entityPath.getDateTime("id", Long.class));

        return new OrderSpecifier[]{primaryOrder, secondaryOrder};
    }

}