package com.library.seatsystem.service;

import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.repository.ReservationRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * D 计算与统计分析 —— 统计分析模块。
 *
 * <p>组合 {@link UsageCalculator}（纯计算）与 {@link ReportExporter}（导出），
 * 对外提供使用率、平均时长、高峰时段与报表导出能力。
 */
@Service
public class StatisticsService extends BaseService<Reservation, Long> {

    private final UsageCalculator usageCalculator;
    private final ReportExporter reportExporter;

    public StatisticsService(
            ReservationRepository reservationRepository,
            UsageCalculator usageCalculator,
            ReportExporter reportExporter
    ) {
        super(reservationRepository);
        this.usageCalculator = usageCalculator;
        this.reportExporter = reportExporter;
    }

/** 计算座位使用率。 */
    public double calcUsageRate(Long roomId, LocalDate date) {
        return usageCalculator.calcUsageRate(roomId, date);
    }

/** 计算平均预约时长。 */
    public double calcAvgDuration(Long roomId, LocalDate date) {
        return usageCalculator.calcAvgDuration(roomId, date);
    }

/** 统计分时段预约次数。 */
    public int[] calcPeakHours(Long roomId, LocalDate date) {
        return usageCalculator.calcPeakHours(roomId, date);
    }

    /**
     * 按周聚合使用率：取从 {@code weekStart} 起连续 7 天的每日使用率。
     *
     * <p>单日使用率由 D 模块的 {@link UsageCalculator} 计算，本方法只做转发，
     * 保证统计口径唯一，不产生第二套计算逻辑。
     *
     * @param roomId    自习室 ID
     * @param weekStart 周的起始日期（含）
     * @return 长度为 7 的使用率数组，下标 0 对应 weekStart 当天
     */
    public double[] calcWeeklyUsageRate(Long roomId, LocalDate weekStart) {
        return usageCalculator.calcWeeklyUsageRate(roomId, weekStart);
    }

    /** 按预约开始时间倒序。 */
    public List<Reservation> sortByUsage(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            return List.of();
        }
        return reservations.stream()
                .sorted(Comparator.comparing(Reservation::getStartTime).reversed())
                .toList();
    }

/** 导出统计明细。 */
    public String exportReport(Long roomId, LocalDate date) {
        List<Reservation> rows = usageCalculator.reservationsOfDay(roomId, date);
        return reportExporter.exportReport("usage-" + roomId + "-" + date, rows);
    }

/** 导出分时段图表数据。 */
    public String exportChart(Long roomId, LocalDate date) {
        return reportExporter.exportChart(usageCalculator.calcPeakHours(roomId, date));
    }
}
