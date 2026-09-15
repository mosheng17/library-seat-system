package com.library.seatsystem.controller;

import com.library.seatsystem.common.ApiResponse;
import com.library.seatsystem.common.BaseController;
import com.library.seatsystem.entity.SysLog;
import com.library.seatsystem.service.SysLogService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A 系统基建与权限 —— 操作日志控制器。
 */
@RestController
@RequestMapping("/api/logs")
public class SysLogController extends BaseController {

    private final SysLogService sysLogService;

    public SysLogController(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    @GetMapping
    public ApiResponse<List<SysLog>> list(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return ok("查询成功", sysLogService.listAll());
        }
        return ok("查询成功", sysLogService.listByUser(userId));
    }
}
