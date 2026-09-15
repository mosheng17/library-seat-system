package com.library.seatsystem.service;

import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.repository.ReservationRepository;
import com.library.seatsystem.repository.SeatRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * C 核心业务流程 —— 座位查询模块。
 *
 * <p>按自习室与时段查询空闲 / 占用座位，是预约管理的前置查询能力。
 */
@Service
public class SeatQueryService extends BaseService<Seat, Long> {

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public SeatQueryService(SeatRepository seatRepository, ReservationRepository reservationRepository) {
        super(seatRepository);
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }

    /** 查询给定时段内空闲的座位。 */
    public List<Seat> listFree(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Seat> result = new ArrayList<>();
        for (Seat seat : seatRepository.findByStudyRoomId(roomId)) {
            if (!com.library.seatsystem.common.BizConstants.SEAT_DISABLED.equalsIgnoreCase(seat.getStatus())
                    && !reservationRepository.existsConflictingReservation(seat.getId(), startTime, endTime)) {
                result.add(seat);
            }
        }
        return result;
    }

    /** 查询给定时段内已被占用的座位。 */
    public List<Seat> listOccupied(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Seat> result = new ArrayList<>();
        for (Seat seat : seatRepository.findByStudyRoomId(roomId)) {
            if (reservationRepository.existsConflictingReservation(seat.getId(), startTime, endTime)) {
                result.add(seat);
            }
        }
        return result;
    }
}
