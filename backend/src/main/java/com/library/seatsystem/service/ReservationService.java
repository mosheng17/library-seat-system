package com.library.seatsystem.service;

import com.library.seatsystem.dto.ReservationRequest;
import com.library.seatsystem.dto.ReservationResponse;
import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.entity.User;
import com.library.seatsystem.exception.BusinessException;
import com.library.seatsystem.repository.ReservationRepository;
import com.library.seatsystem.repository.SeatRepository;
import com.library.seatsystem.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            SeatRepository seatRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        validateReservationTime(request.getStartTime(), request.getEndTime());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new BusinessException("座位不存在"));

        boolean hasConflict = reservationRepository.existsConflictingReservation(
                request.getSeatId(),
                request.getStartTime(),
                request.getEndTime()
        );
        if (hasConflict) {
            throw new BusinessException("该时间段座位已被预约");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setSeat(seat);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus("RESERVED");

        Reservation savedReservation = reservationRepository.save(reservation);
        seat.setStatus("RESERVED");
        seatRepository.save(seat);

        return toResponse(savedReservation);
    }

    @Transactional
    public String cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException("预约记录不存在"));

        if (!"RESERVED".equals(reservation.getStatus())) {
            throw new BusinessException("该预约已取消或不可操作");
        }

        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);

        Seat seat = reservation.getSeat();
        seat.setStatus("AVAILABLE");
        seatRepository.save(seat);

        return "取消预约成功";
    }

    public List<ReservationResponse> getReservationsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("用户不存在");
        }

        return reservationRepository.findByUserIdOrderByStartTimeDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateReservationTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }
    }

    private ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getUser().getRealName(),
                reservation.getSeat().getId(),
                reservation.getSeat().getSeatCode(),
                reservation.getSeat().getStudyRoom().getRoomName(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus()
        );
    }
}
