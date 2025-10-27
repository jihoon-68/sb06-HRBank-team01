package com.sprint.hrbank_sb6_1.repository;

import com.sprint.hrbank_sb6_1.domain.ChangeDiff;
import com.sprint.hrbank_sb6_1.domain.ChangeLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeDiffRepository extends JpaRepository<ChangeDiff, Long> {

    List<ChangeDiff> findByChangeLog(ChangeLog changeLog);
}
