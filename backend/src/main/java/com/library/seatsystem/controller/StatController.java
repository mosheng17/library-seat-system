package com.library.seatsystem.controller;

import com.library.seatsystem.common.ApiResponse;
import com.library.seatsystem.common.BaseController;
import com.library.seatsystem.service.StatisticsService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * D 计算与统计分析 —— 统计分析控制器。
 */
@RestController
@RequestMapping("/api/stats")
public class StatController extends BaseController {

    private final StatisticsService statisticsService;

    public StatController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

/** 查询某自习室某天的座位使用率。 */
    @GetMapping("/usage-rate")
    public ApiResponse<Double> usageRate(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ok("统计成功", statisticsService.calcUsageRate(roomId, date));
    }

/** 查询某自习室某天的平均预约时长。 */
    @GetMapping("/avg-duration")
    public ApiResponse<Double> avgDuration(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ok("统计成功", statisticsService.calcAvgDuration(roomId, date));
    }

/** 查询某自习室某天的分时段预约次数。 */
    @GetMapping("/peak-hours")
    public ApiResponse<int[]> peakHours(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ok("统计成功", statisticsService.calcPeakHours(roomId, date));
    }

    /**
     * 查询某自习室从指定日期起连续 7 天的每日使用率（按周聚合）。
     *
     * @param roomId    自习室 ID
     * @param weekStart 周的起始日期，格式 yyyy-MM-dd
     * @return 长度为 7 的使用率数组，下标 0 对应 weekStart 当天
     */
    @GetMapping("/weekly-usage")
    public ApiResponse<double[]> weeklyUsage(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart
    ) {
        return ok("统计成功", statisticsService.calcWeeklyUsageRate(roomId, weekStart));
    }

/** 导出统计明细 CSV 文本。 */
    @GetMapping("/export")
    public ApiResponse<String> export(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ok("导出成功", statisticsService.exportReport(roomId, date));
    }

/** 导出分时段柱状图数据串。 */
    @GetMapping("/chart")
    public ApiResponse<String> chart(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ok("导出成功", statisticsService.exportChart(roomId, date));
    }
}
