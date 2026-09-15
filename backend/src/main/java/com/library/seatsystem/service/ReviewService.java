package com.library.seatsystem.service;

import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.exception.BusinessException;
import com.library.seatsystem.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * C 核心业务流程 —— 预约审核模块。
 *
 * <p>管理员对待审核预约执行通过 / 驳回，驱动预约状态流转。
 */
@Service
public class ReviewService extends BaseService<Reservation, Long> {

    private final ReservationRepository reservationRepository;

    public ReviewService(ReservationRepository reservationRepository) {
        super(reservationRepository);
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Reservation approve(Long reservationId) {
        return changeStatus(reservationId, "APPROVED");
    }

    @Transactional
    public Reservation reject(Long reservationId) {
        return changeStatus(reservationId, "REJECTED");
    }

    private Reservation changeStatus(Long reservationId, String target) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException("预约记录不存在"));
        if (!"PENDING".equalsIgnoreCase(reservation.getStatus())
                && !"RESERVED".equalsIgnoreCase(reservation.getStatus())) {
            throw new BusinessException("当前状态不可审核：" + reservation.getStatus());
        }
        reservation.setStatus(target);
        return reservationRepository.save(reservation);
    }
}
