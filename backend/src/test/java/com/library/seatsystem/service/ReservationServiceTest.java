package com.library.seatsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.library.seatsystem.common.BizConstants;
import com.library.seatsystem.dto.ReservationRequest;
import com.library.seatsystem.dto.ReservationResponse;
import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.entity.StudyRoom;
import com.library.seatsystem.entity.User;
import com.library.seatsystem.exception.BusinessException;
import com.library.seatsystem.repository.ReservationRepository;
import com.library.seatsystem.repository.SeatRepository;
import com.library.seatsystem.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * C 预约管理模块 ReservationService 的单元测试。
 *
 * <p>覆盖创建预约的 6 条前置校验分支、取消预约的状态机分支、按用户查询的分支。
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ConflictChecker conflictChecker;

    private ReservationService reservationService;

    private static final Long USER_ID = 10L;
    private static final Long SEAT_ID = 20L;

    /** 用未来的时间，避免命中“开始时间不能早于当前时间”分支。 */
    private final LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0);
    private final LocalDateTime end = LocalDateTime.now().plusDays(1).withHour(11).withMinute(0);

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(
                reservationRepository, userRepository, seatRepository, conflictChecker);
    }

    private ReservationRequest request() {
        ReservationRequest request = new ReservationRequest();
        request.setUserId(USER_ID);
        request.setSeatId(SEAT_ID);
        request.setStartTime(start);
        request.setEndTime(end);
        return request;
    }

    private User user() {
        User user = new User();
        user.setId(USER_ID);
        user.setUsername("student01");
        user.setRealName("张三");
        user.setRole(BizConstants.ROLE_STUDENT);
        return user;
    }

    private Seat seat() {
        StudyRoom room = new StudyRoom();
        room.setId(100L);
        room.setRoomName("一楼自习室");
        room.setFloor(1);
        room.setCapacity(40);

        Seat seat = new Seat();
        seat.setId(SEAT_ID);
        seat.setSeatCode("A01");
        seat.setStatus(BizConstants.SEAT_AVAILABLE);
        seat.setStudyRoom(room);
        return seat;
    }

    private Reservation reservation(String status) {
        Reservation reservation = new Reservation();
        reservation.setId(500L);
        reservation.setUser(user());
        reservation.setSeat(seat());
        reservation.setStartTime(start);
        reservation.setEndTime(end);
        reservation.setStatus(status);
        return reservation;
    }

    // ---------- createReservation ----------

    @Test
    @DisplayName("创建预约：结束时间早于开始时间时抛出业务异常")
    void createReversedTime() {
        ReservationRequest request = request();
        request.setEndTime(start.minusHours(1));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> reservationService.createReservation(request));
        assertEquals("结束时间必须晚于开始时间", ex.getMessage());
    }

    @Test
    @DisplayName("创建预约：开始时间早于当前时间时抛出业务异常")
    void createPastTime() {
        ReservationRequest request = request();
        request.setStartTime(LocalDateTime.now().minusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(1));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> reservationService.createReservation(request));
        assertEquals("开始时间不能早于当前时间", ex.getMessage());
    }

    @Test
    @DisplayName("创建预约：用户不存在时抛出业务异常")
    void createUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> reservationService.createReservation(request()));
        assertEquals("用户不存在", ex.getMessage());
    }

    @Test
    @DisplayName("创建预约：座位不存在时抛出业务异常")
    void createSeatNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));
        when(seatRepository.findById(SEAT_ID)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> reservationService.createReservation(request()));
        assertEquals("座位不存在", ex.getMessage());
    }

    @Test
    @DisplayName("创建预约：同一用户该时段已有预约时抛出业务异常（防重复占座）")
    void createUserOverlap() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));
        when(seatRepository.findById(SEAT_ID)).thenReturn(Optional.of(seat()));
        when(reservationRepository.existsUserConflictingReservation(eq(USER_ID), any(), any()))
                .thenReturn(true);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> reservationService.createReservation(request()));
        assertEquals("您在该时间段已有预约，不能重复预约", ex.getMessage());
    }

    @Test
    @DisplayName("创建预约：目标座位该时段已被预约时抛出业务异常")
    void createSeatConflict() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));
        when(seatRepository.findById(SEAT_ID)).thenReturn(Optional.of(seat()));
        when(reservationRepository.existsUserConflictingReservation(eq(USER_ID), any(), any()))
                .thenReturn(false);
        when(conflictChecker.checkConflict(eq(SEAT_ID), any(), any())).thenReturn(true);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> reservationService.createReservation(request()));
        assertEquals("该时间段座位已被预约", ex.getMessage());
    }

    @Test
    @DisplayName("创建预约：正常场景返回预约响应，且座位状态置为 RESERVED")
    void createSuccess() {
        Seat seat = seat();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));
        when(seatRepository.findById(SEAT_ID)).thenReturn(Optional.of(seat));
        when(reservationRepository.existsUserConflictingReservation(eq(USER_ID), any(), any()))
                .thenReturn(false);
        when(conflictChecker.checkConflict(eq(SEAT_ID), any(), any())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(seatRepository.save(any(Seat.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservationResponse response = reservationService.createReservation(request());

        assertNotNull(response);
        assertEquals(USER_ID, response.getUserId());
        assertEquals("张三", response.getRealName());
        assertEquals(SEAT_ID, response.getSeatId());
        assertEquals("A01", response.getSeatCode());
        assertEquals("一楼自习室", response.getRoomName());
        assertEquals(BizConstants.RESERVATION_RESERVED, response.getStatus());
        assertEquals(BizConstants.SEAT_RESERVED, seat.getStatus());
    }

    // ---------- cancelReservation ----------

    @Test
    @DisplayName("取消预约：预约记录不存在时抛出业务异常")
    void cancelNotFound() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> reservationService.cancelReservation(999L));
        assertEquals("预约记录不存在", ex.getMessage());
    }

    @Test
    @DisplayName("取消预约：已是取消状态时抛出业务异常（防重复取消）")
    void cancelAlreadyCancelled() {
        when(reservationRepository.findById(500L))
                .thenReturn(Optional.of(reservation(BizConstants.RESERVATION_CANCELLED)));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> reservationService.cancelReservation(500L));
        assertEquals("该预约已取消或不可操作", ex.getMessage());
    }

    @Test
    @DisplayName("取消预约：正常场景状态改为 CANCELLED 且座位恢复 AVAILABLE")
    void cancelSuccess() {
        Reservation reservation = reservation(BizConstants.RESERVATION_RESERVED);
        when(reservationRepository.findById(500L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(seatRepository.save(any(Seat.class))).thenAnswer(inv -> inv.getArgument(0));

        String message = reservationService.cancelReservation(500L);

        assertEquals("取消预约成功", message);
        assertEquals(BizConstants.RESERVATION_CANCELLED, reservation.getStatus());
        assertEquals(BizConstants.SEAT_AVAILABLE, reservation.getSeat().getStatus());
    }

    // ---------- getReservationsByUser ----------

    @Test
    @DisplayName("查询用户预约：用户不存在时抛出业务异常")
    void queryUserNotFound() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> reservationService.getReservationsByUser(USER_ID));
        assertEquals("用户不存在", ex.getMessage());
    }

    @Test
    @DisplayName("查询用户预约：该用户没有预约时返回空列表")
    void queryEmpty() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(reservationRepository.findByUserIdOrderByStartTimeDesc(anyLong())).thenReturn(List.of());
        assertTrue(reservationService.getReservationsByUser(USER_ID).isEmpty());
    }

    @Test
    @DisplayName("查询用户预约：返回列表按实体正确映射为响应对象")
    void queryNormal() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(reservationRepository.findByUserIdOrderByStartTimeDesc(anyLong()))
                .thenReturn(List.of(reservation(BizConstants.RESERVATION_RESERVED)));
        List<ReservationResponse> list = reservationService.getReservationsByUser(USER_ID);
        assertEquals(1, list.size());
        assertEquals("A01", list.get(0).getSeatCode());
    }
}
