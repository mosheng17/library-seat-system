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
 * D 计算与统计分析 —— 使用率计算器。
 *
 * <p>S4 详细设计（改进后）把纯计算的职责从统计服务中抽出，便于单独测试与复用。
 */
@Service
public class UsageCalculator {

    /** 每个座位每天的可预约时长（小时），用于计算分母。 */
    private static final int OPEN_HOURS_PER_DAY = 12;

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public UsageCalculator(SeatRepository seatRepository, ReservationRepository reservationRepository) {
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }

    /** 座位使用率 = 已占用的座位时长 /（座位数 × 每日开放时长），结果落在 [0,1]。 */
    public double calcUsageRate(Long roomId, LocalDate date) {
        if (roomId == null || date == null) {
            return 0.0;
        }
        List<Seat> seats = seatRepository.findByStudyRoomId(roomId);
        if (seats == null || seats.isEmpty()) {
            return 0.0;
        }
        double capacity = (double) seats.size() * OPEN_HOURS_PER_DAY;
        if (capacity <= 0) {
            return 0.0;
        }
        double used = totalHours(reservationsOfDay(roomId, date));
        return Math.min(1.0, used / capacity);
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
        for (Reservation r : reservationsOfDay(roomId, date)) {
            LocalDateTime start = r.getStartTime();
            if (start != null) {
                buckets[start.getHour()]++;
            }
        }
        return buckets;
    }

    /** 取某自习室某天的有效预约。 */
    public List<Reservation> reservationsOfDay(Long roomId, LocalDate date) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();
        return reservationRepository.findByRoomAndTimeRange(roomId, from, to);
    }

    private double totalHours(List<Reservation> list) {
        double total = 0.0;
        for (Reservation r : list) {
            if (r.getStartTime() != null && r.getEndTime() != null) {
                total += Duration.between(r.getStartTime(), r.getEndTime()).toMinutes() / 60.0;
            }
        }
        return total;
    }
}
