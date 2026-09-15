package com.library.seatsystem.service;

import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.repository.ReservationRepository;
import com.library.seatsystem.repository.SeatRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * D 计算与统计分析 —— 使用率计算器（纯计算，无状态）。
 *
 * <p>改进前 {@code calcUsageRate} 把"取座位 → 算容量 → 取预约 → 算时长 → 封顶"
 * 全塞在一个方法里，圈复杂度 6 且除零保护与主逻辑交织。
 * 这里按"取数 / 算分母 / 算分子 / 归一化"四步拆成小方法，
 * 每个方法只做一件事，便于单测覆盖边界（空座位表、容量为 0、用量超 100%）。
 */
@Service
public class UsageCalculator {

    /** 每个座位每天的可预约时长（小时），作为使用率的分母基数。 */
    private static final int OPEN_HOURS_PER_DAY = 12;

    /** 使用率上限：即使超订也按 100% 计。 */
    private static final double MAX_USAGE_RATE = 1.0;

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public UsageCalculator(SeatRepository seatRepository, ReservationRepository reservationRepository) {
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }

    /** 使用率 = 已占用座位时长 /（座位数 × 每日开放时长），结果落在 [0,1]。 */
    public double calcUsageRate(Long roomId, LocalDate date) {
        if (roomId == null || date == null) {
            return 0.0;
        }
        List<Seat> seats = seatRepository.findByStudyRoomId(roomId);
        double capacity = capacityHours(seats);
        if (capacity <= 0.0) {
            return 0.0;
        }
        double used = totalHours(reservationsOfDay(roomId, date));
        return clampToMax(used / capacity);
    }

    /** 平均每次预约的时长（小时）。 */
    public double calcAvgDuration(Long roomId, LocalDate date) {
        List<Reservation> list = reservationsOfDay(roomId, date);
        if (list.isEmpty()) {
            return 0.0;
        }
        return totalHours(list) / list.size();
    }

    /** 分时段（0—23 点）的预约次数分布。 */
    public int[] calcPeakHours(Long roomId, LocalDate date) {
        int[] buckets = new int[24];
        for (Reservation reservation : reservationsOfDay(roomId, date)) {
            int hour = startHour(reservation);
            if (hour >= 0) {
                buckets[hour]++;
            }
        }
        return buckets;
    }

    /** 取某自习室某天的预约（供 StatisticsService 复用）。 */
    public List<Reservation> reservationsOfDay(Long roomId, LocalDate date) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();
        return reservationRepository.findByRoomAndTimeRange(roomId, from, to);
    }

    /** 分母：座位数 × 每日开放时长。 */
    private double capacityHours(List<Seat> seats) {
        if (seats == null || seats.isEmpty()) {
            return 0.0;
        }
        return (double) seats.size() * OPEN_HOURS_PER_DAY;
    }

    /** 分子：一批预约的总时长（小时）。 */
    private double totalHours(List<Reservation> list) {
        double total = 0.0;
        for (Reservation reservation : list) {
            total += durationHours(reservation);
        }
        return total;
    }

    private double durationHours(Reservation reservation) {
        if (reservation.getStartTime() == null || reservation.getEndTime() == null) {
            return 0.0;
        }
        return Duration.between(reservation.getStartTime(), reservation.getEndTime()).toMinutes() / 60.0;
    }

    private int startHour(Reservation reservation) {
        return reservation.getStartTime() == null ? -1 : reservation.getStartTime().getHour();
    }

    private double clampToMax(double rate) {
        return Math.min(MAX_USAGE_RATE, rate);
    }
}
