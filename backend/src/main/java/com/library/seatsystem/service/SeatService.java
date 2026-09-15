package com.library.seatsystem.service;

import com.library.seatsystem.common.ResponseMapper;
import com.library.seatsystem.dto.SeatResponse;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.repository.SeatRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SeatService extends BaseService<Seat, Long> {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        super(seatRepository);
        this.seatRepository = seatRepository;
    }

    /** 按自习室查询座位列表。 */
    public List<SeatResponse> getSeatsByRoom(Long roomId) {
        return seatRepository.findByStudyRoomId(roomId).stream()
                .map(ResponseMapper::toSeat)
                .toList();
    }
}
