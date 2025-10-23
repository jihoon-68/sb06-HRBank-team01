package com.sprint.hrbank_sb6_1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sprint.hrbank_sb6_1.domain.ChangeLog;
import com.sprint.hrbank_sb6_1.domain.ChangeLogStatus;
import com.sprint.hrbank_sb6_1.domain.Department;
import com.sprint.hrbank_sb6_1.domain.Employee;
import com.sprint.hrbank_sb6_1.dto.ChangeLogDto;
import com.sprint.hrbank_sb6_1.dto.CursorPageResponseChangeLogDto;
import com.sprint.hrbank_sb6_1.dto.DiffDto;
import com.sprint.hrbank_sb6_1.repository.ChangeLogRepository;
import com.sprint.hrbank_sb6_1.service.changelog.ChangeLogServiceImpl;
import java.sql.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeLogServiceImplTest {

    @Mock
    private ChangeLogRepository changeLogRepository;

    @InjectMocks
    private ChangeLogServiceImpl changeLogService;

    private ChangeLog changeLog;

    @BeforeEach
    void setUp() {
        Department department = new Department();
        department.setId(1L);
        department.setName("HR");
        department.setDescription("HR Bank");
        department.setEstablishedDate(Timestamp.valueOf(LocalDateTime.now()));

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setEmail("taehun8234@asdf.com");
        employee.setEmployeeNumber("E001");
        employee.setPosition("Manager");
        employee.setHireDate(LocalDateTime.now().toLocalDate());
        employee.setStatus(null);
        employee.setDepartment(department);
        employee.setProfileImage(null);

        changeLog = new ChangeLog();
        changeLog.setId(1L);
        changeLog.setDescription("[{\"propertyName\":\"name\",\"before\":\"A\",\"after\":\"B\"}]");
        changeLog.setStatus(ChangeLogStatus.CREATED);
        changeLog.setAddress("127.0.0.1");
        changeLog.setAt(LocalDateTime.now());
        changeLog.setEmployee(employee);
    }

    @Test
    @DisplayName("getChangeLog() — Repository의 JPQL 조건과 Pageable을 이용해 로그를 검색한다")
    void testGetChangeLog() {
        // given
        when(changeLogRepository.searchChangeLogs(
            any(),
            any(ChangeLogStatus.class),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(Pageable.class))
        ).thenReturn(List.of(changeLog));

        // when
        CursorPageResponseChangeLogDto result = changeLogService.getChangeLog(
            "E001",
            "CREATED",
            "메모",
            "127.0.0.1",
            null,
            null,
            null,
            10,
            "id",
            "desc"
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        ChangeLogDto dto = result.content().get(0);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(result.hasNext()).isFalse();

        // ✅ JPQL 호출 검증
        verify(changeLogRepository, times(1))
            .searchChangeLogs(eq("E001"), eq(ChangeLogStatus.CREATED), eq("메모"), eq("127.0.0.1"),
                isNull(), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("getChangeLogDiffs() — ChangeLog.description을 JSON으로 파싱하여 DiffDto 리스트를 반환한다")
    void testGetChangeLogDiffs() {
        // given
        when(changeLogRepository.findById(1L)).thenReturn(Optional.of(changeLog));

        // when
        List<DiffDto> diffs = changeLogService.getChangeLogDiffs(1L);

        // then
        assertThat(diffs).hasSize(1);
        DiffDto first = diffs.get(0);
        assertThat(first.propertyName()).isEqualTo("name");
        assertThat(first.before()).isEqualTo("A");
        assertThat(first.after()).isEqualTo("B");
        verify(changeLogRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getChangeLogDiffs() — 존재하지 않는 ID인 경우 예외 발생")
    void testGetChangeLogDiffs_NotFound() {
        // given
        when(changeLogRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> changeLogService.getChangeLogDiffs(999L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ChangeLog not found with id");
    }

    @Test
    @DisplayName("countChangeLogs() — 기간 내 로그 개수를 반환한다")
    void testCountChangeLogs() {
        // given
        LocalDateTime from = LocalDateTime.now().minusDays(7);
        LocalDateTime to = LocalDateTime.now();
        when(changeLogRepository.countByAtBetween(from, to)).thenReturn(3L);

        // when
        Long count = changeLogService.countChangeLogs(from, to);

        // then
        assertThat(count).isEqualTo(3L);
        verify(changeLogRepository, times(1)).countByAtBetween(from, to);
    }
}

