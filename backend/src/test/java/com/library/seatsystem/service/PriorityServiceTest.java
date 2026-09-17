package com.library.seatsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.library.seatsystem.common.BizConstants;
import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.entity.User;
import com.library.seatsystem.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * D 预约优先级计算模块 PriorityService 的单元测试。
 *
 * <p>评分 = 角色权重 + 时长权重。重点覆盖角色为 null、时间为 null、
 * 时长超过基准值等边界分支。
 */
class PriorityServiceTest {

    private PriorityService priorityService;

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 9, 20, 9, 0);

    @BeforeEach
    void setUp() {
        priorityService = new PriorityService(Mockito.mock(ReservationRepository.class));
    }

    private Reservation reservationWith(String role, Integer durationMinutes) {
        Reservation reservation = new Reservation();
        if (role != null) {
            User user = new User();
            user.setId(1L);
            user.setRole(role);
            reservation.setUser(user);
        }
        if (durationMinutes != null) {
            reservation.setStartTime(BASE);
            reservation.setEndTime(BASE.plusMinutes(durationMinutes));
        }
        return reservation;
    }

    @Test
    @DisplayName("预约对象为 null 时得分返回 0")
    void scoreNullReservation() {
        assertEquals(0, priorityService.calcScore(null));
    }

    @Test
    @DisplayName("教师角色 60 分钟预约：角色权重 100 + 时长权重 60 = 160")
    void scoreTeacher() {
        assertEquals(160, priorityService.calcScore(
                reservationWith(BizConstants.ROLE_TEACHER, 60)));
    }

    @Test
    @DisplayName("管理员角色同样享受高优先级")
    void scoreAdmin() {
        assertEquals(160, priorityService.calcScore(
                reservationWith(BizConstants.ROLE_ADMIN, 60)));
    }

    @Test
    @DisplayName("学生角色：角色权重 0 + 时长权重 60 = 60")
    void scoreStudent() {
        assertEquals(60, priorityService.calcScore(
                reservationWith(BizConstants.ROLE_STUDENT, 60)));
    }

    @Test
    @DisplayName("小写角色 teacher 也能识别为高优先级（大小写不敏感）")
    void scoreLowerCaseRole() {
        assertEquals(160, priorityService.calcScore(reservationWith("teacher", 60)));
    }

    @Test
    @DisplayName("用户对象为 null 时角色权重按普通角色计")
    void scoreNullUser() {
        assertEquals(60, priorityService.calcScore(reservationWith(null, 60)));
    }

    @Test
    @DisplayName("时间字段为 null 时时长权重为 0，只计角色权重")
    void scoreNullTime() {
        assertEquals(100, priorityService.calcScore(
                reservationWith(BizConstants.ROLE_TEACHER, null)));
    }

    @Test
    @DisplayName("时长超过基准 120 分钟时时长权重取 0，不出现负分")
    void scoreLongDuration() {
        assertEquals(100, priorityService.calcScore(
                reservationWith(BizConstants.ROLE_TEACHER, 200)));
    }

    @Test
    @DisplayName("排序：空列表返回空列表")
    void sortEmpty() {
        assertTrue(priorityService.sort(List.of()).isEmpty());
    }

    @Test
    @DisplayName("排序：null 输入返回空列表，不抛空指针")
    void sortNull() {
        assertTrue(priorityService.sort(null).isEmpty());
    }

    @Test
    @DisplayName("排序：高优先级（教师短时预约）排在前面")
    void sortDescending() {
        Reservation teacher = reservationWith(BizConstants.ROLE_TEACHER, 30);
        Reservation student = reservationWith(BizConstants.ROLE_STUDENT, 30);
        List<Reservation> sorted = priorityService.sort(List.of(student, teacher));
        assertEquals(2, sorted.size());
        assertEquals(BizConstants.ROLE_TEACHER, sorted.get(0).getUser().getRole());
    }
}
