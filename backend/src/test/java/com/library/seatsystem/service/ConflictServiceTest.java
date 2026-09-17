package com.library.seatsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.library.seatsystem.common.BizConstants;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.repository.ReservationRepository;
import com.library.seatsystem.repository.SeatRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 靶子模块 ConflictService 的单元测试。
 *
 * <p>按"时段合法性 → 座位可用性 → 时段重叠"三层规则分支逐条设计用例，
 * 覆盖正常、边界、异常三类。全部使用 Mockito 替身，不依赖数据库。
 */
@ExtendWith(MockitoExtension.class)
class ConflictServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    private ConflictService conflictService;

    private static final Long SEAT_ID = 1L;
    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 20, 9, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 9, 20, 11, 0);

    @BeforeEach
    void setUp() {
        conflictService = new ConflictService(reservationRepository, seatRepository);
    }

    private Seat seatWithStatus(String status) {
        Seat seat = new Seat();
        seat.setId(SEAT_ID);
        seat.setSeatCode("A01");
        seat.setStatus(status);
        return seat;
    }

    // ---------- checkSeatStatus ----------

    @Test
    @DisplayName("座位对象为 null 时判定为不可用")
    void checkSeatStatusNullSeat() {
        assertFalse(conflictService.checkSeatStatus(null));
    }

    @Test
    @DisplayName("座位状态为 null 时判定为不可用")
    void checkSeatStatusNullStatus() {
        assertFalse(conflictService.checkSeatStatus(seatWithStatus(null)));
    }

    @Test
    @DisplayName("状态 AVAILABLE 判定为可用")
    void checkSeatStatusAvailable() {
        assertTrue(conflictService.checkSeatStatus(seatWithStatus(BizConstants.SEAT_AVAILABLE)));
    }

    @Test
    @DisplayName("状态 RESERVED 判定为可用（可续约判定）")
    void checkSeatStatusReserved() {
        assertTrue(conflictService.checkSeatStatus(seatWithStatus(BizConstants.SEAT_RESERVED)));
    }

    @Test
    @DisplayName("状态 IN_USE 判定为可用")
    void checkSeatStatusInUse() {
        assertTrue(conflictService.checkSeatStatus(seatWithStatus(BizConstants.SEAT_IN_USE)));
    }

    @Test
    @DisplayName("状态 DISABLED 判定为不可用")
    void checkSeatStatusDisabled() {
        assertFalse(conflictService.checkSeatStatus(seatWithStatus(BizConstants.SEAT_DISABLED)));
    }

    @Test
    @DisplayName("状态 MAINTENANCE 判定为不可用")
    void checkSeatStatusMaintenance() {
        assertFalse(conflictService.checkSeatStatus(seatWithStatus(BizConstants.SEAT_MAINTENANCE)));
    }

    @Test
    @DisplayName("小写状态 available 也能识别为可用（大小写不敏感）")
    void checkSeatStatusLowerCase() {
        assertTrue(conflictService.checkSeatStatus(seatWithStatus("available")));
    }

    @Test
    @DisplayName("未知状态 BROKEN 判定为不可用")
    void checkSeatStatusUnknown() {
        assertFalse(conflictService.checkSeatStatus(seatWithStatus("BROKEN")));
    }

    // ---------- checkTimeOverlap（时段合法性） ----------

    @Test
    @DisplayName("开始时间为 null 时判定时段不合法")
    void checkTimeOverlapNullStart() {
        assertFalse(conflictService.checkTimeOverlap(null, END));
    }

    @Test
    @DisplayName("结束时间为 null 时判定时段不合法")
    void checkTimeOverlapNullEnd() {
        assertFalse(conflictService.checkTimeOverlap(START, null));
    }

    @Test
    @DisplayName("起止时间相同（零长度时段）判定为不合法")
    void checkTimeOverlapSameTime() {
        assertFalse(conflictService.checkTimeOverlap(START, START));
    }

    @Test
    @DisplayName("结束早于开始（倒置时段）判定为不合法")
    void checkTimeOverlapReversed() {
        assertFalse(conflictService.checkTimeOverlap(END, START));
    }

    @Test
    @DisplayName("结束晚于开始判定为合法")
    void checkTimeOverlapValid() {
        assertTrue(conflictService.checkTimeOverlap(START, END));
    }

    // ---------- describeConflict（三层规则的原因判定） ----------

    @Test
    @DisplayName("座位 ID 为 null 时返回“时段不合法”")
    void describeConflictNullSeatId() {
        assertEquals("时段不合法", conflictService.describeConflict(null, START, END));
    }

    @Test
    @DisplayName("时段非法时返回“时段不合法”，且不访问数据库")
    void describeConflictInvalidTime() {
        assertEquals("时段不合法", conflictService.describeConflict(SEAT_ID, START, START));
    }

    @Test
    @DisplayName("座位不存在时返回“座位不存在”")
    void describeConflictSeatNotFound() {
        when(seatRepository.findById(SEAT_ID)).thenReturn(Optional.empty());
        assertEquals("座位不存在", conflictService.describeConflict(SEAT_ID, START, END));
    }

    @Test
    @DisplayName("座位已停用时返回“座位已停用或维修中”")
    void describeConflictSeatDisabled() {
        when(seatRepository.findById(SEAT_ID))
                .thenReturn(Optional.of(seatWithStatus(BizConstants.SEAT_DISABLED)));
        assertEquals("座位已停用或维修中", conflictService.describeConflict(SEAT_ID, START, END));
    }

    @Test
    @DisplayName("该时段已有预约时返回“该时段已被预约”")
    void describeConflictOverlapped() {
        when(seatRepository.findById(SEAT_ID))
                .thenReturn(Optional.of(seatWithStatus(BizConstants.SEAT_AVAILABLE)));
        when(reservationRepository.existsConflictingReservation(eq(SEAT_ID), any(), any())).thenReturn(true);
        assertEquals("该时段已被预约", conflictService.describeConflict(SEAT_ID, START, END));
    }

    @Test
    @DisplayName("座位可用且无重叠时返回 null（无冲突）")
    void describeConflictNoConflict() {
        when(seatRepository.findById(SEAT_ID))
                .thenReturn(Optional.of(seatWithStatus(BizConstants.SEAT_AVAILABLE)));
        when(reservationRepository.existsConflictingReservation(eq(SEAT_ID), any(), any())).thenReturn(false);
        assertNull(conflictService.describeConflict(SEAT_ID, START, END));
    }

    // ---------- checkConflict（对外布尔接口） ----------

    @Test
    @DisplayName("无冲突时 checkConflict 返回 false")
    void checkConflictFalse() {
        when(seatRepository.findById(SEAT_ID))
                .thenReturn(Optional.of(seatWithStatus(BizConstants.SEAT_AVAILABLE)));
        when(reservationRepository.existsConflictingReservation(eq(SEAT_ID), any(), any())).thenReturn(false);
        assertFalse(conflictService.checkConflict(SEAT_ID, START, END));
    }

    @Test
    @DisplayName("存在重叠预约时 checkConflict 返回 true")
    void checkConflictTrue() {
        when(seatRepository.findById(SEAT_ID))
                .thenReturn(Optional.of(seatWithStatus(BizConstants.SEAT_AVAILABLE)));
        when(reservationRepository.existsConflictingReservation(eq(SEAT_ID), any(), any())).thenReturn(true);
        assertTrue(conflictService.checkConflict(SEAT_ID, START, END));
    }
}
