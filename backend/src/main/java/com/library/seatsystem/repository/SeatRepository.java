package com.library.seatsystem.repository;

import com.library.seatsystem.entity.Seat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByStudyRoomId(Long roomId);
    boolean existsByStudyRoomIdAndSeatCode(Long roomId, String seatCode);
}
