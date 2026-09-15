package com.library.seatsystem.common;

import com.library.seatsystem.dto.ReservationResponse;
import com.library.seatsystem.dto.SeatResponse;
import com.library.seatsystem.dto.StudyRoomResponse;
import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.entity.StudyRoom;
import com.library.seatsystem.entity.User;

/**
 * A/B 公共 —— 实体到响应 DTO 的统一映射器。
 *
 * <p>改进前 {@code AdminService}、{@code SeatService}、{@code StudyRoomService}、
 * {@code ReservationService} 各自写了一份几乎相同的转换代码（共 4 处重复）；
 * 这里集中为静态方法，实体字段变更时只需改一处。
 *
 * <p>解决的是"重复代码"与"散弹式修改"两类坏味道。
 *
 * <p>注意：映射时对可空关联（user / seat / studyRoom）做了空值保护。
 * 早期版本把 6 个三元判空直接写进 {@code toReservation}，导致该方法圈复杂度
 * 升到 8（超过 ≤7 的阈值）；现在把判空下沉到 {@code userId}/{@code userName}
 * 等小方法，主方法只保留一次非空判断。
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
                roomId(room),
                roomName(room),
                seat.getSeatCode(),
                seat.getStatus()
        );
    }

    /** 预约实体 → 响应（含用户与座位信息）。 */
    public static ReservationResponse toReservation(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        User user = reservation.getUser();
        Seat seat = reservation.getSeat();
        return new ReservationResponse(
                reservation.getId(),
                userId(user),
                userName(user),
                seatId(seat),
                seatCode(seat),
                roomName(seat == null ? null : seat.getStudyRoom()),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus()
        );
    }

    private static Long userId(User user) {
        return user == null ? null : user.getId();
    }

    private static String userName(User user) {
        return user == null ? null : user.getRealName();
    }

    private static Long seatId(Seat seat) {
        return seat == null ? null : seat.getId();
    }

    private static String seatCode(Seat seat) {
        return seat == null ? null : seat.getSeatCode();
    }

    private static Long roomId(StudyRoom room) {
        return room == null ? null : room.getId();
    }

    private static String roomName(StudyRoom room) {
        return room == null ? null : room.getRoomName();
    }
}
