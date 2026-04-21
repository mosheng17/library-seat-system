package com.library.seatsystem.repository;

import com.library.seatsystem.entity.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserIdOrderByStartTimeDesc(Long userId);

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
}
