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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * B 基础数据管理 —— 数据字典控制器。
 *
 * <p>对外提供字典项的查询、新增、修改与删除接口，供前端下拉框与后台维护页面使用。
 */
@RestController
@RequestMapping("/api/dicts")
public class SysDictController extends BaseController {

    private final SysDictService sysDictService;

    public SysDictController(SysDictService sysDictService) {
        this.sysDictService = sysDictService;
    }

    /**
     * 按类型查询字典项；不传类型时返回全部。
     *
     * @param type 字典类型，可为空
     * @return 字典项列表
     */
    @GetMapping
    public ApiResponse<List<SysDict>> list(@RequestParam(required = false) String type) {
        if (type == null || type.isBlank()) {
            return ok("查询成功", sysDictService.findAll());
        }
        return ok("查询成功", sysDictService.listByType(type));
    }

    /**
     * 按"类型 + 键"查询单个字典项，供前端回显单个选项。
     *
     * @param type 字典类型
     * @param key  字典键
     * @return 匹配的字典项
     */
    @GetMapping("/item")
    public ApiResponse<SysDict> item(@RequestParam String type, @RequestParam String key) {
        return ok("查询成功", sysDictService.findByTypeAndKey(type, key));
    }

    /**
     * 新增字典项。
     *
     * @param dict 待新增的字典项
     * @return 已保存的字典项
     */
    @PostMapping
    public ApiResponse<SysDict> create(@RequestBody SysDict dict) {
        return ok("新增字典成功", sysDictService.create(dict));
    }

    /**
     * 修改字典项的值与排序号。
     *
     * @param id        字典项主键
     * @param dictValue 新的字典值（展示名）
     * @param sort      新的排序号，可为空
     * @return 更新后的字典项
     */
    @PutMapping("/{id}")
    public ApiResponse<SysDict> update(
            @PathVariable Long id,
            @RequestParam String dictValue,
            @RequestParam(required = false) Integer sort
    ) {
        return ok("修改成功", sysDictService.updateValue(id, dictValue, sort));
    }

    /**
     * 删除字典项。
     *
     * @param id 字典项主键
     * @return 空数据响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sysDictService.delete(id);
        return ok("删除成功", null);
    }
}
