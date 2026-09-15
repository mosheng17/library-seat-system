package com.library.seatsystem.service;

import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.repository.ReservationRepository;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * D 计算与统计分析 —— 优先级计算模块。
 *
 * <p>评分规则：教师/管理员加权 + 时长越短优先级越高（提高座位周转率），
 * 冲突时用该得分排序决定谁先占用座位。
 */
@Service
public class PriorityService extends BaseService<Reservation, Long> implements PriorityCalculator {

    /** 高优先级角色加权。 */
    private static final int ROLE_WEIGHT_TEACHER = 100;
    private static final int ROLE_WEIGHT_STUDENT = 0;

    public PriorityService(ReservationRepository reservationRepository) {
        super(reservationRepository);
    }

    @Override
    public List<Reservation> sort(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            return List.of();
        }
        return reservations.stream()
                .sorted(Comparator.comparingInt(this::calcScore).reversed())
                .toList();
    }

    @Override
    public int calcScore(Reservation reservation) {
        if (reservation == null) {
            return 0;
        }
        int score = roleWeight(reservation);
        if (reservation.getStartTime() != null && reservation.getEndTime() != null) {
            long minutes = Duration.between(reservation.getStartTime(), reservation.getEndTime()).toMinutes();
            score += (int) Math.max(0, 120 - minutes);
        }
        return score;
    }

    private int roleWeight(Reservation reservation) {
        if (reservation.getUser() == null) {
            return ROLE_WEIGHT_STUDENT;
        }
        String role = reservation.getUser().getRole();
        if (role != null && ("TEACHER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role))) {
            return ROLE_WEIGHT_TEACHER;
        }
        return ROLE_WEIGHT_STUDENT;
    }
}
