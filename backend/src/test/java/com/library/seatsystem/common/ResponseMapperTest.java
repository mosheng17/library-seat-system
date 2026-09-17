package com.library.seatsystem.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.library.seatsystem.dto.ReservationResponse;
import com.library.seatsystem.dto.SeatResponse;
import com.library.seatsystem.dto.StudyRoomResponse;
import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.entity.StudyRoom;
import com.library.seatsystem.entity.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 统一响应映射器 ResponseMapper 的单元测试。
 *
 * <p>该类是 S5 阶段为消除 4 处重复 DTO 转换而抽取的，并且曾经因为
 * 6 个三元判空塞进 toReservation 导致圈复杂度升到 8。
 * 本测试重点覆盖各条可空关联分支，确保空值保护没有遗漏。
 */
class ResponseMapperTest {

    private StudyRoom room() {
        StudyRoom room = new StudyRoom();
        room.setId(100L);
        room.setRoomName("一楼自习室");
        room.setFloor(1);
        room.setCapacity(40);
        return room;
    }

    private User user() {
        User user = new User();
        user.setId(10L);
        user.setUsername("student01");
        user.setRealName("张三");
        user.setRole(BizConstants.ROLE_STUDENT);
        return user;
    }

    private Seat seat(StudyRoom room) {
        Seat seat = new Seat();
        seat.setId(20L);
        seat.setSeatCode("A01");
        seat.setStatus(BizConstants.SEAT_AVAILABLE);
        seat.setStudyRoom(room);
        return seat;
    }

    // ---------- toReservation ----------

    @Test
    @DisplayName("预约映射：入参为 null 时返回 null")
    void toReservationNull() {
        assertNull(ResponseMapper.toReservation(null));
    }

    @Test
    @DisplayName("预约映射：完整对象的所有字段正确映射")
    void toReservationFull() {
        Reservation reservation = new Reservation();
        reservation.setId(500L);
        reservation.setUser(user());
        reservation.setSeat(seat(room()));
        reservation.setStartTime(LocalDateTime.of(2026, 9, 20, 9, 0));
        reservation.setEndTime(LocalDateTime.of(2026, 9, 20, 11, 0));
        reservation.setStatus(BizConstants.RESERVATION_RESERVED);

        ReservationResponse response = ResponseMapper.toReservation(reservation);

        assertNotNull(response);
        assertEquals(500L, response.getId().longValue());
        assertEquals(10L, response.getUserId().longValue());
        assertEquals("张三", response.getRealName());
        assertEquals(20L, response.getSeatId().longValue());
        assertEquals("A01", response.getSeatCode());
        assertEquals("一楼自习室", response.getRoomName());
        assertEquals(BizConstants.RESERVATION_RESERVED, response.getStatus());
    }

    @Test
    @DisplayName("预约映射：用户关联为 null 时用户字段返回 null，不抛空指针")
    void toReservationNullUser() {
        Reservation reservation = new Reservation();
        reservation.setId(501L);
        reservation.setUser(null);
        reservation.setSeat(seat(room()));

        ReservationResponse response = ResponseMapper.toReservation(reservation);

        assertNull(response.getUserId());
        assertNull(response.getRealName());
        assertEquals("A01", response.getSeatCode());
    }

    @Test
    @DisplayName("预约映射：座位关联为 null 时座位字段返回 null，不抛空指针")
    void toReservationNullSeat() {
        Reservation reservation = new Reservation();
        reservation.setId(502L);
        reservation.setUser(user());
        reservation.setSeat(null);

        ReservationResponse response = ResponseMapper.toReservation(reservation);

        assertNull(response.getSeatId());
        assertNull(response.getSeatCode());
        assertNull(response.getRoomName());
        assertEquals("张三", response.getRealName());
    }

    @Test
    @DisplayName("预约映射：座位不空但自习室关联为 null 时自习室名返回 null")
    void toReservationNullRoom() {
        Reservation reservation = new Reservation();
        reservation.setId(503L);
        reservation.setUser(user());
        reservation.setSeat(seat(null));

        ReservationResponse response = ResponseMapper.toReservation(reservation);

        assertNull(response.getRoomName());
        assertEquals("A01", response.getSeatCode());
    }

    // ---------- toSeat ----------

    @Test
    @DisplayName("座位映射：入参为 null 时返回 null")
    void toSeatNull() {
        assertNull(ResponseMapper.toSeat(null));
    }

    @Test
    @DisplayName("座位映射：完整对象正确带上所属自习室信息")
    void toSeatFull() {
        SeatResponse response = ResponseMapper.toSeat(seat(room()));
        assertEquals(20L, response.getId().longValue());
        assertEquals("A01", response.getSeatCode());
        assertEquals(100L, response.getRoomId().longValue());
        assertEquals("一楼自习室", response.getRoomName());
    }

    @Test
    @DisplayName("座位映射：自习室关联为 null 时房间字段返回 null，不抛空指针")
    void toSeatNullRoom() {
        SeatResponse response = ResponseMapper.toSeat(seat(null));
        assertNull(response.getRoomId());
        assertNull(response.getRoomName());
        assertEquals("A01", response.getSeatCode());
    }

    // ---------- toStudyRoom ----------

    @Test
    @DisplayName("自习室映射：入参为 null 时返回 null")
    void toStudyRoomNull() {
        assertNull(ResponseMapper.toStudyRoom(null));
    }

    @Test
    @DisplayName("自习室映射：字段正确映射")
    void toStudyRoomFull() {
        StudyRoomResponse response = ResponseMapper.toStudyRoom(room());
        assertEquals(100L, response.getId().longValue());
        assertEquals("一楼自习室", response.getRoomName());
        assertEquals(1, response.getFloor().intValue());
        assertEquals(40, response.getCapacity().intValue());
    }
}
