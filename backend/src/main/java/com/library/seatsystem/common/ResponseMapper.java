package com.library.seatsystem.common;

import com.library.seatsystem.dto.ReservationResponse;
import com.library.seatsystem.dto.SeatResponse;
import com.library.seatsystem.dto.StudyRoomResponse;
import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.entity.StudyRoom;

/**
 * A/B 公共 —— 实体到响应 DTO 的统一映射器。
 *
 * <p>改进前 {@code AdminService}、{@code SeatService}、{@code StudyRoomService}、
 * {@code ReservationService} 各自写了一份几乎相同的转换代码（共 4 处重复）；
 * 这里集中为静态方法，实体字段变更时只需改一处。
 *
 * <p>解决的是"重复代码"与"散弹式修改"两类坏味道。
 */
public final class ResponseMapper {

    private ResponseMapper() {
    }

    /** 自习室实体 → 响应。 */
    public static StudyRoomResponse toStudyRoom(StudyRoom room) {
        if (room == null) {
            return null;
        }
        return new StudyRoomResponse(
                room.getId(),
                room.getRoomName(),
                room.getFloor(),
                room.getCapacity()
        );
    }

    /** 座位实体 → 响应（含所属自习室信息）。 */
    public static SeatResponse toSeat(Seat seat) {
        if (seat == null) {
            return null;
        }
        StudyRoom room = seat.getStudyRoom();
        return new SeatResponse(
                seat.getId(),
                room == null ? null : room.getId(),
                room == null ? null : room.getRoomName(),
                seat.getSeatCode(),
                seat.getStatus()
        );
    }

    /** 预约实体 → 响应（含用户与座位信息）。 */
    public static ReservationResponse toReservation(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        Seat seat = reservation.getSeat();
        StudyRoom room = seat == null ? null : seat.getStudyRoom();
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser() == null ? null : reservation.getUser().getId(),
                reservation.getUser() == null ? null : reservation.getUser().getRealName(),
                seat == null ? null : seat.getId(),
                seat == null ? null : seat.getSeatCode(),
                room == null ? null : room.getRoomName(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus()
        );
    }
}
