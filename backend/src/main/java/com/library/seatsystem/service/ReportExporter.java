package com.library.seatsystem.service;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * D 计算与统计分析 —— 报表导出器。
 *
 * <p>改进前两个导出方法里混着字符串拼接与循环，且 {@code exportChart} 的
 * "是否首元素" 判断让圈复杂度升到 5。现在把拼接抽成
 * {@link #appendBucket}，主方法只负责遍历。
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

    /** 导出分时段柱状图所需的数据串，格式："0:3,1:5,..."。 */
    public String exportChart(int[] buckets) {
        if (buckets == null || buckets.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int hour = 0; hour < buckets.length; hour++) {
            appendBucket(sb, hour, buckets[hour]);
        }
        return sb.toString();
    }

    /** 追加一个 "小时:次数" 片段，非首项时补逗号分隔符。 */
    private void appendBucket(StringBuilder sb, int hour, int count) {
        if (sb.length() > 0) {
            sb.append(',');
        }
        sb.append(hour).append(':').append(count);
    }
}
