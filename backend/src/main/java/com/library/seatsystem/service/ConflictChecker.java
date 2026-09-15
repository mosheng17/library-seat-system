package com.library.seatsystem.service;

import java.time.LocalDateTime;

/**
 * D 计算与统计分析 —— 冲突检测能力接口。
 *
 * <p>S4 详细设计（改进后）把"冲突规则计算"抽象为接口，
 * 由 {@link ConflictService} 实现，调用方（预约管理）依赖接口而非实现。
 */
public interface ConflictChecker {

    /** 判断指定座位在给定时段是否冲突。 */
    boolean checkConflict(Long seatId, LocalDateTime startTime, LocalDateTime endTime);
}
