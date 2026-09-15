package com.library.seatsystem.service;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * D 计算与统计分析 —— 报表导出器。
 *
 * <p>把统计结果导出为 CSV 文本 / 简易图表用的数据串，供前端下载或绘图。
 */
@Service
public class ReportExporter {

    /** 导出统计明细为 CSV。 */
    public String exportReport(String title, List<?> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("report,").append(title).append('\n');
        for (Object row : rows) {
            sb.append(row).append('\n');
        }
        return sb.toString();
    }

    /** 导出分时段柱状图所需的数据串（"0:3,1:5,..."）。 */
    public String exportChart(int[] buckets) {
        if (buckets == null || buckets.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buckets.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(i).append(':').append(buckets[i]);
        }
        return sb.toString();
    }
}
