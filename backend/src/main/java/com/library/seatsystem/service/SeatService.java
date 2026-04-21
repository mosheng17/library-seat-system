package com.library.seatsystem.service;

import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.repository.SeatRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<Seat> getSeatsByRoom(Long roomId) {
        return seatRepository.findByStudyRoomId(roomId);
    }
}

