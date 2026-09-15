package com.library.seatsystem.service;

import com.library.seatsystem.common.BizConstants;
import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.repository.ReservationRepository;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * D 计算与统计分析 —— 预约优先级计算模块。
 *
 * <p>评分 = 角色权重 + 时长权重（时长越短得分越高，以提高座位周转率）。
 *
 * <p>改进前 {@code roleWeight} 用 {@code role != null && ("TEACHER".equals || "ADMIN".equals)}
 * 这样的长条件链，且把 {@code 120}、{@code 100} 等魔法数写死在方法里；
 * 现在提为常量与 {@link Set} 判断，分支更少、语义更清楚。
 */
@Service
public class PriorityService extends BaseService<Reservation, Long> implements PriorityCalculator {

    /** 高优先级角色加权。 */
    private static final int ROLE_WEIGHT_HIGH = 100;

    /** 普通角色加权。 */
    private static final int ROLE_WEIGHT_NORMAL = 0;

    /** 时长权重基准（分钟）：时长越短，加分越多。 */
    private static final int DURATION_BASE_MINUTES = 120;

    /** 享受高优先级的角色集合。 */
    private static final Set<String> HIGH_PRIORITY_ROLES = Set.of(
            BizConstants.ROLE_TEACHER,
            BizConstants.ROLE_ADMIN
    );

    public PriorityService(ReservationRepository reservationRepository) {
        super(reservationRepository);
    }

/** 按优先级降序排序。 */
    @Override
    public List<Reservation> sort(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            return List.of();
        }
        return reservations.stream()
                .sorted(Comparator.comparingInt(this::calcScore).reversed())
                .toList();
    }

/** 计算优先级得分（角色权重 + 时长权重）。 */
    @Override
    public int calcScore(Reservation reservation) {
        if (reservation == null) {
            return 0;
        }
        return roleWeight(reservation) + durationWeight(reservation);
    }

    /** 角色权重：教师/管理员优先。 */
    private int roleWeight(Reservation reservation) {
        if (reservation.getUser() == null || reservation.getUser().getRole() == null) {
            return ROLE_WEIGHT_NORMAL;
        }
        String role = reservation.getUser().getRole().toUpperCase(Locale.ROOT);
        return HIGH_PRIORITY_ROLES.contains(role) ? ROLE_WEIGHT_HIGH : ROLE_WEIGHT_NORMAL;
    }

    /** 时长权重：越短越高，最长不超过基准值。 */
    private int durationWeight(Reservation reservation) {
        if (reservation.getStartTime() == null || reservation.getEndTime() == null) {
            return 0;
        }
        long minutes = Duration.between(reservation.getStartTime(), reservation.getEndTime()).toMinutes();
        return (int) Math.max(0, DURATION_BASE_MINUTES - minutes);
    }
}
