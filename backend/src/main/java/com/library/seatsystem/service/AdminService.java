package com.library.seatsystem.service;

import com.library.seatsystem.common.ResponseMapper;
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
public class AdminService extends BaseService<StudyRoom, Long> {

    private final StudyRoomRepository studyRoomRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public AdminService(
            StudyRoomRepository studyRoomRepository,
            SeatRepository seatRepository,
            ReservationRepository reservationRepository
    ) {
        super(studyRoomRepository);
        this.studyRoomRepository = studyRoomRepository;
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }

/** 查询全部自习室。 */
    public List<StudyRoomResponse> getAllRooms() {
        return studyRoomRepository.findAll().stream()
                .map(ResponseMapper::toStudyRoom)
                .toList();
    }

/** 新增自习室（名称唯一）。 */
    @Transactional
    public StudyRoomResponse createRoom(CreateStudyRoomRequest request) {
        if (studyRoomRepository.existsByRoomName(request.getRoomName())) {
            throw new BusinessException("自习室名称已存在");
        }

        StudyRoom room = new StudyRoom();
        room.setRoomName(request.getRoomName());
        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());

        return ResponseMapper.toStudyRoom(studyRoomRepository.save(room));
    }

/** 查询某自习室的座位。 */
    public List<SeatResponse> getSeatsByRoom(Long roomId) {
        return seatRepository.findByStudyRoomId(roomId).stream()
                .map(ResponseMapper::toSeat)
                .toList();
    }

/** 新增座位（同室内座号唯一）。 */
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

        return ResponseMapper.toSeat(seatRepository.save(seat));
    }

/** 查询全部预约。 */
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAllByOrderByStartTimeDesc().stream()
                .map(ResponseMapper::toReservation)
                .toList();
    }
}

