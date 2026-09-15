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

    @GetMapping("/usage-rate")
    public ApiResponse<Double> usageRate(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ok("统计成功", statisticsService.calcUsageRate(roomId, date));
    }

    @GetMapping("/avg-duration")
    public ApiResponse<Double> avgDuration(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ok("统计成功", statisticsService.calcAvgDuration(roomId, date));
    }

    @GetMapping("/peak-hours")
    public ApiResponse<int[]> peakHours(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ok("统计成功", statisticsService.calcPeakHours(roomId, date));
    }

    @GetMapping("/export")
    public ApiResponse<String> export(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ok("导出成功", statisticsService.exportReport(roomId, date));
    }

    @GetMapping("/chart")
    public ApiResponse<String> chart(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ok("导出成功", statisticsService.exportChart(roomId, date));
    }
}
