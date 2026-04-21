package com.library.seatsystem.service;

import com.library.seatsystem.dto.CreateSeatRequest;
import com.library.seatsystem.dto.CreateStudyRoomRequest;
import com.library.seatsystem.dto.ReservationResponse;
import com.library.seatsystem.dto.SeatResponse;
import com.library.seatsystem.dto.StudyRoomResponse;
import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.entity.StudyRoom;
import com.library.seatsystem.exception.BusinessException;
import com.library.seatsystem.repository.ReservationRepository;
import com.library.seatsystem.repository.SeatRepository;
import com.library.seatsystem.repository.StudyRoomRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final StudyRoomRepository studyRoomRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public AdminService(
            StudyRoomRepository studyRoomRepository,
            SeatRepository seatRepository,
            ReservationRepository reservationRepository
    ) {
        this.studyRoomRepository = studyRoomRepository;
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<StudyRoomResponse> getAllRooms() {
        return studyRoomRepository.findAll().stream()
                .map(this::toStudyRoomResponse)
                .toList();
    }

    @Transactional
    public StudyRoomResponse createRoom(CreateStudyRoomRequest request) {
        if (studyRoomRepository.existsByRoomName(request.getRoomName())) {
            throw new BusinessException("自习室名称已存在");
        }

        StudyRoom room = new StudyRoom();
        room.setRoomName(request.getRoomName());
        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());

        return toStudyRoomResponse(studyRoomRepository.save(room));
    }

    public List<SeatResponse> getSeatsByRoom(Long roomId) {
        return seatRepository.findByStudyRoomId(roomId).stream()
                .map(this::toSeatResponse)
                .toList();
    }

    @Transactional
    public SeatResponse createSeat(CreateSeatRequest request) {
        StudyRoom room = studyRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new BusinessException("自习室不存在"));

        if (seatRepository.existsByStudyRoomIdAndSeatCode(request.getRoomId(), request.getSeatCode())) {
            throw new BusinessException("该自习室下座位编号已存在");
        }

        Seat seat = new Seat();
        seat.setStudyRoom(room);
        seat.setSeatCode(request.getSeatCode());
        seat.setStatus(request.getStatus());

        return toSeatResponse(seatRepository.save(seat));
    }

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAllByOrderByStartTimeDesc().stream()
                .map(this::toReservationResponse)
                .toList();
    }

    private StudyRoomResponse toStudyRoomResponse(StudyRoom room) {
        return new StudyRoomResponse(
                room.getId(),
                room.getRoomName(),
                room.getFloor(),
                room.getCapacity()
        );
    }

    private SeatResponse toSeatResponse(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getStudyRoom().getId(),
                seat.getStudyRoom().getRoomName(),
                seat.getSeatCode(),
                seat.getStatus()
        );
    }

    private ReservationResponse toReservationResponse(Reservation reservation) {
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

