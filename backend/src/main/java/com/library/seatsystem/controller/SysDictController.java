package com.library.seatsystem.controller;

import com.library.seatsystem.common.ApiResponse;
import com.library.seatsystem.common.BaseController;
import com.library.seatsystem.entity.SysDict;
import com.library.seatsystem.service.SysDictService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * B 基础数据管理 —— 数据字典控制器。
 */
@RestController
@RequestMapping("/api/dicts")
public class SysDictController extends BaseController {

    private final SysDictService sysDictService;

    public SysDictController(SysDictService sysDictService) {
        this.sysDictService = sysDictService;
    }

    @GetMapping
    public ApiResponse<List<SysDict>> list(@RequestParam(required = false) String type) {
        if (type == null || type.isBlank()) {
            return ok("查询成功", sysDictService.findAll());
        }
        return ok("查询成功", sysDictService.listByType(type));
    }

    @PostMapping
    public ApiResponse<SysDict> create(@RequestBody SysDict dict) {
        return ok("新增字典成功", sysDictService.create(dict));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sysDictService.delete(id);
        return ok("删除成功", null);
    }
}
