package com.library.seatsystem.service;

import com.library.seatsystem.entity.Reservation;
import java.util.List;

/**
 * D 计算与统计分析 —— 预约优先级计算能力接口。
 */
public interface PriorityCalculator {

    /** 按优先级排序（高优先级在前）。 */
    List<Reservation> sort(List<Reservation> reservations);

    /** 计算单条预约的优先级得分。 */
    int calcScore(Reservation reservation);
}
