package com.library.seatsystem.repository;

import com.library.seatsystem.entity.Seat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * B 基础数据管理 —— 座位仓储。
 */
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByStudyRoomId(Long roomId);
    boolean existsByStudyRoomIdAndSeatCode(Long roomId, String seatCode);
}
