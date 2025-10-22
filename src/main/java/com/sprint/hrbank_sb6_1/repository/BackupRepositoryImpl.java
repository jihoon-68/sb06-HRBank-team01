package com.sprint.hrbank_sb6_1.repository;

import com.sprint.hrbank_sb6_1.domain.Backup;
import com.sprint.hrbank_sb6_1.dto.SearchBackupRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
public class BackupRepositoryImpl implements BackupRepositoryCustom {
    private final EntityManager em;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("startedAt", "endedAt", "status", "id");

    private static class JpqlWhere {
        final String clause;
        final Map<String, Object> params;

        JpqlWhere(String clause, Map<String, Object> params) {
            this.clause = clause;
            this.params = params;
        }
    }

    // --- [헬퍼 메소드] 중복되는 WHERE 로직 추출 ---
    private JpqlWhere buildDynamicWhereClause(SearchBackupRequest search) {
        StringBuilder whereClause = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        // worker 부분일치
        if (StringUtils.hasText(search.getWorker())) {
            whereClause.append(" AND backup.worker LIKE :worker ");
            params.put("worker", "%" + search.getWorker() + "%");
        }
        // status 완잔일치
        if (search.getStatus() != null) {
            whereClause.append(" AND backup.status = :status ");
            params.put("status", search.getStatus());
        }
        // startedAtFrom 범위일치
        if (search.getStartedAtFrom() != null) {
            whereClause.append(" AND backup.startedAt >= :startedAtFrom ");
            params.put("startedAtFrom", search.getStartedAtFrom());
        }
        // startedAtTo 범위일치
        if (search.getStartedAtTo() != null) {
            whereClause.append(" AND backup.startedAt <= :startedAtTo ");
            params.put("startedAtTo", search.getStartedAtTo());
        }

        return new JpqlWhere(whereClause.toString(), params);
    }

    @Override
    public Slice<Backup> searchTasks(SearchBackupRequest search) {

        JpqlWhere where = buildDynamicWhereClause(search);

        StringBuilder sql = new StringBuilder("SELECT backup FROM Backup backup WHERE 1=1");
        sql.append(where.clause);

        // 커서 로직
        if (StringUtils.hasText(search.getCursor()) && "startedAt".equals(search.getSortField())) {
            LocalDateTime cursorValue = LocalDateTime.parse(search.getCursor());
            if ("DESC".equalsIgnoreCase(search.getSortDirection())) {
                // 내림차순
                sql.append(" AND backup.startedAt < :cursorValue ");
            } else {
                // 오름차순
                sql.append(" AND backup.startedAt > :cursorValue ");
            }
            where.params.put("cursorValue", cursorValue);
        }

        // 정렬
        String sortField = "id";
        if (StringUtils.hasText(search.getSortField()) && ALLOWED_SORT_FIELDS.contains(search.getSortField())) {
            sortField = search.getSortField();
        }
        String sortDirection = "DESC";
        if ("ASC".equalsIgnoreCase(search.getSortDirection())) {
            sortDirection = "ASC";
        }
        sql.append(" ORDER BY backup.").append(sortField).append(" ").append(sortDirection);
        sql.append(", backup.id DESC");

        // 쿼리 생성, 파라미터/페이징 설정
        TypedQuery<Backup> query = em.createQuery(sql.toString(), Backup.class);
        where.params.forEach(query::setParameter);

        //Size + 1 트릭 적용
        int requestSize = search.getSize();
        query.setMaxResults(requestSize + 1);

        List<Backup> content = query.getResultList();

        //"Size + 1" 트릭으로 hasNext 계산
        boolean hasNext = false;
        if (content.size() > requestSize) {
            hasNext = true;
            content.remove(requestSize); // 마지막 (size + 1)번째 아이템은 리스트에서 제거
        }

        Pageable pageable = PageRequest.of(0, requestSize);

        return new SliceImpl<>(content, pageable, hasNext);


    }

    @Override
    public long countTasks(SearchBackupRequest search) {
        JpqlWhere where = buildDynamicWhereClause(search);
        TypedQuery<Long> countQuery = em.createQuery("SELECT COUNT(backup) FROM Backup backup WHERE 1=1" + where.clause, Long.class);
        where.params.forEach(countQuery::setParameter);
        return countQuery.getSingleResult();
    }
}
