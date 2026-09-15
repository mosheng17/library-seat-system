package com.library.seatsystem.controller;

import com.library.seatsystem.common.ApiResponse;
import com.library.seatsystem.common.BaseController;
import com.library.seatsystem.dto.StudyRoomResponse;
import com.library.seatsystem.service.StudyRoomService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * B 基础数据管理 —— 自习室查询接口。
 */
@RestController
@RequestMapping("/api/rooms")
public class StudyRoomController extends BaseController {

    private final StudyRoomService studyRoomService;

    public StudyRoomController(StudyRoomService studyRoomService) {
        this.studyRoomService = studyRoomService;
    }

/** 查询全部自习室。 */
    @GetMapping
    public ApiResponse<List<StudyRoomResponse>> getAllRooms() {
        return ok("查询成功", studyRoomService.getAllRooms());
    }
}

