package com.library.seatsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * D 报表导出器 ReportExporter 的单元测试。
 *
 * <p>覆盖改进后抽出 appendBucket 的两个拼接方法的边界：
 * null 数组、空数组、单元素、多元素的分隔符处理。
 */
class ReportExporterTest {

    private ReportExporter reportExporter;

    @BeforeEach
    void setUp() {
        reportExporter = new ReportExporter();
    }

    @Test
    @DisplayName("导出明细：标题与每行数据各占一行")
    void exportReportNormal() {
        String csv = reportExporter.exportReport("usage-1-2026-09-20", List.of("row1", "row2"));
        assertEquals("report,usage-1-2026-09-20\nrow1\nrow2\n", csv);
    }

    @Test
    @DisplayName("导出明细：数据行列表为空时只输出标题行")
    void exportReportEmpty() {
        assertEquals("report,t\n", reportExporter.exportReport("t", List.of()));
    }

    @Test
    @DisplayName("导出图表数据：null 数组返回空串")
    void exportChartNull() {
        assertEquals("", reportExporter.exportChart(null));
    }

    @Test
    @DisplayName("导出图表数据：空数组返回空串")
    void exportChartEmpty() {
        assertEquals("", reportExporter.exportChart(new int[0]));
    }

    @Test
    @DisplayName("导出图表数据：多个桶用逗号分隔，格式为 小时:次数")
    void exportChartNormal() {
        assertEquals("0:1,1:0,2:3", reportExporter.exportChart(new int[]{1, 0, 3}));
    }

    @Test
    @DisplayName("导出图表数据：只有一个桶时不出现多余逗号")
    void exportChartSingle() {
        String result = reportExporter.exportChart(new int[]{7});
        assertEquals("0:7", result);
        assertTrue(!result.contains(","));
    }
}
