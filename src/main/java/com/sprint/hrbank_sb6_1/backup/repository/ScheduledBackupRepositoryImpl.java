package com.sprint.hrbank_sb6_1.backup.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.hrbank_sb6_1.backup.dto.ScheduledBackupResponseDto;
import com.sprint.hrbank_sb6_1.backup.dto.ScheduledBackupSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduledBackupRepositoryImpl implements ScheduledBackupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ScheduledBackupResponseDto> search(ScheduledBackupSearchCondition condition) {

        // 여기에 QueryDSL 동적 쿼리 구현 예정

        return List.of(); // 임시
    }
}