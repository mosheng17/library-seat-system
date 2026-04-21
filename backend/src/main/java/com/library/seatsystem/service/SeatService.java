package com.library.seatsystem.service;

import com.library.seatsystem.dto.SeatResponse;
import com.library.seatsystem.repository.SeatRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<SeatResponse> getSeatsByRoom(Long roomId) {
        return seatRepository.findByStudyRoomId(roomId).stream()
                .map(seat -> new SeatResponse(
                        seat.getId(),
                        seat.getStudyRoom().getId(),
                        seat.getStudyRoom().getRoomName(),
                        seat.getSeatCode(),
                        seat.getStatus()
                ))
                .toList();
    }
}
