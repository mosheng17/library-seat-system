package com.library.seatsystem.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.repository.ReservationRepository;
import com.library.seatsystem.repository.SeatRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * D 使用率计算器 UsageCalculator 的单元测试。
 *
 * <p>重点覆盖拆分后的四个边界：空座位表、容量为 0、用量超 100%、空预约列表。
 */
@ExtendWith(MockitoExtension.class)
class UsageCalculatorTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ReservationRepository reservationRepository;

    private UsageCalculator calculator;

    private static final Long ROOM_ID = 1L;
    private static final LocalDate DATE = LocalDate.of(2026, 9, 20);

    @BeforeEach
    void setUp() {
        calculator = new UsageCalculator(seatRepository, reservationRepository);
    }

    private List<Seat> seats(int count) {
        List<Seat> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Seat seat = new Seat();
            seat.setId((long) (i + 1));
            seat.setSeatCode("A0" + (i + 1));
            list.add(seat);
        }
        return list;
    }

    private Reservation reservation(int startHour, int endHour) {
        Reservation r = new Reservation();
        r.setStartTime(LocalDateTime.of(2026, 9, 20, startHour, 0));
        r.setEndTime(LocalDateTime.of(2026, 9, 20, endHour, 0));
        return r;
    }

    // ---------- calcUsageRate ----------

    @Test
    @DisplayName("自习室 ID 为 null 时使用率返回 0")
    void usageRateNullRoom() {
        assertEquals(0.0, calculator.calcUsageRate(null, DATE));
    }

    @Test
    @DisplayName("日期为 null 时使用率返回 0")
    void usageRateNullDate() {
        assertEquals(0.0, calculator.calcUsageRate(ROOM_ID, null));
    }

    @Test
    @DisplayName("自习室没有座位（容量为 0）时使用率返回 0，不抛除零异常")
    void usageRateNoSeats() {
        when(seatRepository.findByStudyRoomId(ROOM_ID)).thenReturn(List.of());
        assertEquals(0.0, calculator.calcUsageRate(ROOM_ID, DATE));
    }

    @Test
    @DisplayName("正常场景：2 个座位容量 24 小时、占用 3 小时，使用率 0.125")
    void usageRateNormal() {
        when(seatRepository.findByStudyRoomId(ROOM_ID)).thenReturn(seats(2));
        when(reservationRepository.findByRoomAndTimeRange(eq(ROOM_ID), any(), any()))
                .thenReturn(List.of(reservation(9, 12)));
        assertEquals(0.125, calculator.calcUsageRate(ROOM_ID, DATE), 1e-9);
    }

    @Test
    @DisplayName("超订场景：占用时长超过容量时使用率封顶为 1.0")
    void usageRateClamped() {
        when(seatRepository.findByStudyRoomId(ROOM_ID)).thenReturn(seats(1));
        when(reservationRepository.findByRoomAndTimeRange(eq(ROOM_ID), any(), any()))
                .thenReturn(List.of(reservation(0, 23)));
        assertEquals(1.0, calculator.calcUsageRate(ROOM_ID, DATE), 1e-9);
    }

    // ---------- calcAvgDuration ----------

    @Test
    @DisplayName("当天没有预约时平均时长返回 0")
    void avgDurationEmpty() {
        when(reservationRepository.findByRoomAndTimeRange(eq(ROOM_ID), any(), any()))
                .thenReturn(List.of());
        assertEquals(0.0, calculator.calcAvgDuration(ROOM_ID, DATE));
    }

    @Test
    @DisplayName("正常场景：两笔预约 2 小时与 4 小时，平均 3 小时")
    void avgDurationNormal() {
        when(reservationRepository.findByRoomAndTimeRange(eq(ROOM_ID), any(), any()))
                .thenReturn(List.of(reservation(9, 11), reservation(13, 17)));
        assertEquals(3.0, calculator.calcAvgDuration(ROOM_ID, DATE), 1e-9);
    }

    // ---------- calcPeakHours ----------

    @Test
    @DisplayName("当天没有预约时分时段数组为全 0")
    void peakHoursEmpty() {
        when(reservationRepository.findByRoomAndTimeRange(eq(ROOM_ID), any(), any()))
                .thenReturn(List.of());
        assertArrayEquals(new int[24], calculator.calcPeakHours(ROOM_ID, DATE));
    }

    @Test
    @DisplayName("按时段正确分桶计数")
    void peakHoursNormal() {
        when(reservationRepository.findByRoomAndTimeRange(eq(ROOM_ID), any(), any()))
                .thenReturn(List.of(reservation(9, 11), reservation(9, 12), reservation(14, 16)));
        int[] buckets = calculator.calcPeakHours(ROOM_ID, DATE);
        assertEquals(2, buckets[9]);
        assertEquals(1, buckets[14]);
        assertEquals(0, buckets[10]);
    }

    @Test
    @DisplayName("开始时间为 null 的脏数据被跳过，不计入任何时段")
    void peakHoursNullStartTime() {
        Reservation broken = new Reservation();
        broken.setStartTime(null);
        broken.setEndTime(LocalDateTime.of(2026, 9, 20, 10, 0));
        when(reservationRepository.findByRoomAndTimeRange(eq(ROOM_ID), any(), any()))
                .thenReturn(List.of(broken));
        assertArrayEquals(new int[24], calculator.calcPeakHours(ROOM_ID, DATE));
    }

    // ---------- calcWeeklyUsageRate ----------

    @Test
    @DisplayName("按周聚合：自习室 ID 为 null 时返回长度 7 的全 0 数组")
    void weeklyNullRoom() {
        double[] rates = calculator.calcWeeklyUsageRate(null, DATE);
        assertEquals(7, rates.length);
        assertArrayEquals(new double[7], rates);
    }

    @Test
    @DisplayName("按周聚合：起始日期为 null 时返回长度 7 的全 0 数组")
    void weeklyNullWeekStart() {
        double[] rates = calculator.calcWeeklyUsageRate(ROOM_ID, null);
        assertEquals(7, rates.length);
        assertArrayEquals(new double[7], rates);
    }

    @Test
    @DisplayName("按周聚合：返回 7 天的每日使用率，下标 0 对应周起始日")
    void weeklyNormal() {
        when(seatRepository.findByStudyRoomId(ROOM_ID)).thenReturn(seats(2));
        when(reservationRepository.findByRoomAndTimeRange(eq(ROOM_ID), any(), any()))
                .thenReturn(List.of(reservation(9, 12)));
        double[] rates = calculator.calcWeeklyUsageRate(ROOM_ID, DATE);
        assertEquals(7, rates.length);
        for (double rate : rates) {
            assertEquals(0.125, rate, 1e-9);
        }
    }
}
