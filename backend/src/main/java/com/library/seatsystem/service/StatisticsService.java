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

    public double calcUsageRate(Long roomId, LocalDate date) {
        return usageCalculator.calcUsageRate(roomId, date);
    }

    public double calcAvgDuration(Long roomId, LocalDate date) {
        return usageCalculator.calcAvgDuration(roomId, date);
    }

    public int[] calcPeakHours(Long roomId, LocalDate date) {
        return usageCalculator.calcPeakHours(roomId, date);
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

    public String exportReport(Long roomId, LocalDate date) {
        List<Reservation> rows = usageCalculator.reservationsOfDay(roomId, date);
        return reportExporter.exportReport("usage-" + roomId + "-" + date, rows);
    }

    public String exportChart(Long roomId, LocalDate date) {
        return reportExporter.exportChart(usageCalculator.calcPeakHours(roomId, date));
    }
}
