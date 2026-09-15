package com.library.seatsystem.controller;

import com.library.seatsystem.common.ApiResponse;
import com.library.seatsystem.common.BaseController;
import com.library.seatsystem.dto.CreateSeatRequest;
import com.library.seatsystem.dto.CreateStudyRoomRequest;
import com.library.seatsystem.dto.ReservationResponse;
import com.library.seatsystem.dto.SeatResponse;
import com.library.seatsystem.dto.StudyRoomResponse;
import com.library.seatsystem.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A 系统基建与权限 —— 管理端接口（自习室 / 座位 / 预约）。
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController extends BaseController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

/** 查询全部自习室。 */
    @GetMapping("/rooms")
    public ApiResponse<List<StudyRoomResponse>> getAllRooms() {
        return ok("查询成功", adminService.getAllRooms());
    }

/** 新增自习室。 */
    @PostMapping("/rooms")
    public ApiResponse<StudyRoomResponse> createRoom(@Valid @RequestBody CreateStudyRoomRequest request) {
        return ok("新增自习室成功", adminService.createRoom(request));
    }

/** 查询某自习室的座位。 */
    @GetMapping("/rooms/{roomId}/seats")
    public ApiResponse<List<SeatResponse>> getSeatsByRoom(@PathVariable Long roomId) {
        return ok("查询成功", adminService.getSeatsByRoom(roomId));
    }

/** 新增座位。 */
    @PostMapping("/seats")
    public ApiResponse<SeatResponse> createSeat(@Valid @RequestBody CreateSeatRequest request) {
        return ok("新增座位成功", adminService.createSeat(request));
    }

/** 查询全部预约（按开始时间倒序）。 */
    @GetMapping("/reservations")
    public ApiResponse<List<ReservationResponse>> getAllReservations() {
        return ok("查询成功", adminService.getAllReservations());
    }
}

