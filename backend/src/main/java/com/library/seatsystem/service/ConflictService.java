package com.library.seatsystem.service;

import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.repository.ReservationRepository;
import com.library.seatsystem.repository.SeatRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * D 计算与统计分析 —— 冲突检测模块（分支最密集，是 V(G)/Max Depth 的靶子模块）。
 *
 * <p>规则：时段合法性 → 座位可用性 → 时段重叠 三层判定。
 */
@Service
public class ConflictService extends BaseService<Reservation, Long> implements ConflictChecker {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    public ConflictService(ReservationRepository reservationRepository, SeatRepository seatRepository) {
        super(reservationRepository);
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public boolean checkConflict(Long seatId, LocalDateTime startTime, LocalDateTime endTime) {
        if (seatId == null) {
            return true;
        }
        if (!checkTimeOverlap(startTime, endTime)) {
            return true;
        }
        Seat seat = seatRepository.findById(seatId).orElse(null);
        if (!checkSeatStatus(seat)) {
            return true;
        }
        return reservationRepository.existsConflictingReservation(seatId, startTime, endTime);
    }

    /** 时段是否合法且存在重叠区间。 */
    public boolean checkTimeOverlap(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }
        return endTime.isAfter(startTime);
    }

    /** 座位当前是否可用（未被停用/维修）。 */
    public boolean checkSeatStatus(Seat seat) {
        if (seat == null) {
            return false;
        }
        String status = seat.getStatus();
        if ("DISABLED".equalsIgnoreCase(status) || "MAINTENANCE".equalsIgnoreCase(status)) {
            return false;
        }
        return "AVAILABLE".equalsIgnoreCase(status) || "RESERVED".equalsIgnoreCase(status) || "IN_USE".equalsIgnoreCase(status);
    }

    /** 冲突原因描述，供前端提示；无冲突返回 null。 */
    public String describeConflict(Long seatId, LocalDateTime startTime, LocalDateTime endTime) {
        if (!checkTimeOverlap(startTime, endTime)) {
            return "时段不合法";
        }
        Seat seat = seatRepository.findById(seatId).orElse(null);
        if (seat == null) {
            return "座位不存在";
        }
        if (!checkSeatStatus(seat)) {
            return "座位已停用或维修中";
        }
        if (reservationRepository.existsConflictingReservation(seatId, startTime, endTime)) {
            return "该时段已被预约";
        }
        return null;
    }
}
