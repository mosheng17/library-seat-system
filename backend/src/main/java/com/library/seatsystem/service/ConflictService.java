package com.library.seatsystem.service;

import com.library.seatsystem.common.BizConstants;
import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.repository.ReservationRepository;
import com.library.seatsystem.repository.SeatRepository;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * D 计算与统计分析 —— 冲突检测模块。
 *
 * <p>冲突规则分三层判定：时段合法性 → 座位可用性 → 时段重叠。
 * 该模块是"复杂度靶子"模块：改进前 {@code checkSeatStatus} 用 5 个 {@code ||}
 * 串起状态判断、{@code checkConflict} 与 {@code describeConflict} 逻辑重叠，
 * 圈复杂度偏高、分支难以覆盖。
 *
 * <p>改进手段：
 * <ol>
 *   <li>把"可用/不可用状态"提为 {@link Set} 常量，长条件链变成一次集合包含判断；</li>
 *   <li>{@code checkConflict} 只保留"是否有冲突"的语义，具体原因交给
 *       {@link #describeConflict}，消除两处重复的分支；</li>
 *   <li>把数据访问细节收敛到私有方法 {@link #hasOverlappingReservation}。</li>
 * </ol>
 */
@Service
public class ConflictService extends BaseService<Reservation, Long> implements ConflictChecker {

    /** 视为可用的座位状态。 */
    private static final Set<String> USABLE_STATUS = Set.of(
            BizConstants.SEAT_AVAILABLE,
            BizConstants.SEAT_RESERVED,
            BizConstants.SEAT_IN_USE
    );

    /** 视为不可用的座位状态。 */
    private static final Set<String> UNUSABLE_STATUS = Set.of(
            BizConstants.SEAT_DISABLED,
            BizConstants.SEAT_MAINTENANCE
    );

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    public ConflictService(ReservationRepository reservationRepository, SeatRepository seatRepository) {
        super(reservationRepository);
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public boolean checkConflict(Long seatId, LocalDateTime startTime, LocalDateTime endTime) {
        return describeConflict(seatId, startTime, endTime) != null;
    }

    /**
     * 返回冲突原因，无冲突时返回 {@code null}。
     *
     * @return "时段不合法" / "座位不存在" / "座位已停用或维修中" / "该时段已被预约"，或 null
     */
    public String describeConflict(Long seatId, LocalDateTime startTime, LocalDateTime endTime) {
        if (seatId == null || !checkTimeOverlap(startTime, endTime)) {
            return "时段不合法";
        }
        Seat seat = seatRepository.findById(seatId).orElse(null);
        if (seat == null) {
            return "座位不存在";
        }
        if (!checkSeatStatus(seat)) {
            return "座位已停用或维修中";
        }
        if (hasOverlappingReservation(seatId, startTime, endTime)) {
            return "该时段已被预约";
        }
        return null;
    }

    /** 时段是否合法：两个端点都不为空，且结束晚于开始。 */
    public boolean checkTimeOverlap(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }
        return endTime.isAfter(startTime);
    }

    /** 座位当前是否可用（既不在不可用集合里，又在可用集合里）。 */
    public boolean checkSeatStatus(Seat seat) {
        if (seat == null || seat.getStatus() == null) {
            return false;
        }
        String status = seat.getStatus().toUpperCase(Locale.ROOT);
        return !UNUSABLE_STATUS.contains(status) && USABLE_STATUS.contains(status);
    }

    /** 数据访问细节：该座位在该时段是否已有有效预约。 */
    private boolean hasOverlappingReservation(Long seatId, LocalDateTime startTime, LocalDateTime endTime) {
        return reservationRepository.existsConflictingReservation(seatId, startTime, endTime);
    }
}
