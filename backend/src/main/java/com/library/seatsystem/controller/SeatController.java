package com.library.seatsystem.controller;

import com.library.seatsystem.common.ApiResponse;
import com.library.seatsystem.common.BaseController;
import com.library.seatsystem.dto.SeatResponse;
import com.library.seatsystem.service.SeatService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * B 基础数据管理 —— 座位查询接口。
 */
@RestController
@RequestMapping("/api/seats")
public class SeatController extends BaseController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

/** 按自习室查询座位列表。 */
    @GetMapping("/room/{roomId}")
    public ApiResponse<List<SeatResponse>> getSeatsByRoom(@PathVariable Long roomId) {
        return ok("查询成功", seatService.getSeatsByRoom(roomId));
    }
}
