package com.library.seatsystem.repository;

import com.library.seatsystem.entity.Reservation;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * C 核心业务流程 —— 预约仓储。
 *
 * <p>除常规 CRUD 外，还提供冲突检测（座位时段重叠 / 用户时段重叠）与
 * 按自习室+时段查询，分别供 C 模块的预约校验与 D 模块的统计分析使用。
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserIdOrderByStartTimeDesc(Long userId);
    List<Reservation> findAllByOrderByStartTimeDesc();

    /** 该座位在给定时段内是否已有未取消的预约。 */
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
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 该用户在给定时段内是否已有未取消的预约。
     *
     * <p>用于防止同一用户在同一时间段预约多个座位。
     * 与座位冲突不同，这里只要"用户 + 时段"重叠即视为冲突，
     * 因此取消状态（CANCELLED）的记录不参与判断。
     */
    @Query("""
            select count(r) > 0
            from Reservation r
            where r.user.id = :userId
              and r.status <> 'CANCELLED'
              and r.startTime < :endTime
              and r.endTime > :startTime
            """)
    boolean existsUserConflictingReservation(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
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
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
