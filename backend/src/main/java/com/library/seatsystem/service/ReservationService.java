package com.library.seatsystem.service;

import com.library.seatsystem.common.BizConstants;
import com.library.seatsystem.common.ResponseMapper;
import com.library.seatsystem.dto.ReservationRequest;
import com.library.seatsystem.dto.ReservationResponse;
import com.library.seatsystem.entity.Reservation;
import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.entity.User;
import com.library.seatsystem.exception.BusinessException;
import com.library.seatsystem.repository.ReservationRepository;
import com.library.seatsystem.repository.SeatRepository;
import com.library.seatsystem.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * C 核心业务流程 —— 预约管理模块。
 *
 * <p>负责预约的创建、取消与查询，是"申请 → 审核 → 执行 → 完成"主干的入口。
 *
 * <p>改进点（业务校验补全）：改进前创建预约只校验了"座位"是否被占用，
 * 没有校验"用户"是否已被占用，导致同一个人可以在同一时间段预约多个座位。
 * 现在新增 {@link #assertNoUserOverlap}，把"用户时段重叠"也纳入创建前置校验。
 *
 * <p>改进点（依赖倒置）：冲突判定不再直接调用
 * {@code ReservationRepository.existsConflictingReservation}，
 * 而是依赖 {@link ConflictChecker} 接口，把"规则计算"的职责交回 D 模块。
 * 这样 C→D 的耦合由"依赖具体实现 + 具体查询方法"降为"依赖接口"，
 * 后续替换冲突算法（如按优先级排队）不需要改动本类。
 */
@Service
public class ReservationService extends BaseService<Reservation, Long> {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final ConflictChecker conflictChecker;

    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            SeatRepository seatRepository,
            ConflictChecker conflictChecker
    ) {
        super(reservationRepository);
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.conflictChecker = conflictChecker;
    }

    /**
     * 创建预约。
     *
     * @param request 预约请求（用户、座位、起止时间）
     * @return 新建预约的响应
     * @throws BusinessException 时间不合法 / 用户或座位不存在 / 时段冲突 / 该用户此时段已有预约
     */
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        validateReservationTime(request.getStartTime(), request.getEndTime());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new BusinessException("座位不存在"));

        assertNoUserOverlap(request);

        if (conflictChecker.checkConflict(request.getSeatId(), request.getStartTime(), request.getEndTime())) {
            throw new BusinessException("该时间段座位已被预约");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setSeat(seat);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus(BizConstants.RESERVATION_RESERVED);

        Reservation savedReservation = reservationRepository.save(reservation);
        seat.setStatus(BizConstants.SEAT_RESERVED);
        seatRepository.save(seat);

        return ResponseMapper.toReservation(savedReservation);
    }

    /** 取消预约，并把座位恢复为空闲状态。 */
    @Transactional
    public String cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException("预约记录不存在"));

        if (!BizConstants.RESERVATION_RESERVED.equals(reservation.getStatus())) {
            throw new BusinessException("该预约已取消或不可操作");
        }

        reservation.setStatus(BizConstants.RESERVATION_CANCELLED);
        reservationRepository.save(reservation);

        Seat seat = reservation.getSeat();
        seat.setStatus(BizConstants.SEAT_AVAILABLE);
        seatRepository.save(seat);

        return "取消预约成功";
    }

    /** 查询某用户的全部预约，按开始时间倒序。 */
    public List<ReservationResponse> getReservationsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("用户不存在");
        }
        return reservationRepository.findByUserIdOrderByStartTimeDesc(userId).stream()
                .map(ResponseMapper::toReservation)
                .toList();
    }

    /** 校验预约时段：结束必须晚于开始，且开始不能早于当前时间。 */
    private void validateReservationTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }
    }

    /**
     * 校验同一用户在同一时段是否已有其它预约。
     *
     * <p>与座位冲突检测相互独立：座位冲突回答"这个座位有没有人占"，
     * 本方法回答"这个人是不是已经约了别的地方"，两者都要通过才能创建成功。
     *
     * @param request 预约请求
     * @throws BusinessException 该用户在此时间段已有预约
     */
    private void assertNoUserOverlap(ReservationRequest request) {
        boolean overlapped = reservationRepository.existsUserConflictingReservation(
                request.getUserId(), request.getStartTime(), request.getEndTime());
        if (overlapped) {
            throw new BusinessException("您在该时间段已有预约，不能重复预约");
        }
    }
}
