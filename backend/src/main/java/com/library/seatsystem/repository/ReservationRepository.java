package com.library.seatsystem.repository;

import com.library.seatsystem.entity.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * C 核心业务流程 —— 预约仓储。
 *
 * <p>除常规 CRUD 外，还提供冲突检测（时段重叠）与按自习室+时段查询，
 * 分别供 D 模块的冲突检测与统计分析使用。
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserIdOrderByStartTimeDesc(Long userId);
    List<Reservation> findAllByOrderByStartTimeDesc();

    @Query("""
            select count(r) > 0
            from Reservation r
            where r.seat.id = :seatId
              and r.status = 'RESERVED'
              and r.startTime < :endTime
              and r.endTime > :startTime
            """)
    boolean existsConflictingReservation(
            @Param("seatId") Long seatId,
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime
    );

    /** D 统计分析用：取某自习室在给定时段内的预约。 */
    @Query("""
            select r
            from Reservation r
            where r.seat.studyRoom.id = :roomId
              and r.startTime >= :from
              and r.startTime < :to
            """)
    List<Reservation> findByRoomAndTimeRange(
            @Param("roomId") Long roomId,
            @Param("from") java.time.LocalDateTime from,
            @Param("to") java.time.LocalDateTime to
    );
}
