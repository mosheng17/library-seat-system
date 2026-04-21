package com.library.seatsystem.controller;

import com.library.seatsystem.common.ApiResponse;
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

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/rooms")
    public ApiResponse<List<StudyRoomResponse>> getAllRooms() {
        return ApiResponse.success("查询成功", adminService.getAllRooms());
    }

    @PostMapping("/rooms")
    public ApiResponse<StudyRoomResponse> createRoom(@Valid @RequestBody CreateStudyRoomRequest request) {
        return ApiResponse.success("新增自习室成功", adminService.createRoom(request));
    }

    @GetMapping("/rooms/{roomId}/seats")
    public ApiResponse<List<SeatResponse>> getSeatsByRoom(@PathVariable Long roomId) {
        return ApiResponse.success("查询成功", adminService.getSeatsByRoom(roomId));
    }

    @PostMapping("/seats")
    public ApiResponse<SeatResponse> createSeat(@Valid @RequestBody CreateSeatRequest request) {
        return ApiResponse.success("新增座位成功", adminService.createSeat(request));
    }

    @GetMapping("/reservations")
    public ApiResponse<List<ReservationResponse>> getAllReservations() {
        return ApiResponse.success("查询成功", adminService.getAllReservations());
    }
}

