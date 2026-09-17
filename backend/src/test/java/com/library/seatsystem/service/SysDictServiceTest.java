package com.library.seatsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.library.seatsystem.entity.SysDict;
import com.library.seatsystem.exception.BusinessException;
import com.library.seatsystem.repository.SysDictRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * B 数据字典模块 SysDictService 的单元测试。
 *
 * <p>覆盖按类型查询、按"类型+键"精确查询、新增去重、修改时的空值校验等分支。
 */
@ExtendWith(MockitoExtension.class)
class SysDictServiceTest {

    @Mock
    private SysDictRepository sysDictRepository;

    private SysDictService sysDictService;

    private static final String TYPE = "SEAT_STATUS";

    @BeforeEach
    void setUp() {
        sysDictService = new SysDictService(sysDictRepository);
    }

    private SysDict dict(String key, String value, Integer sort) {
        SysDict d = new SysDict();
        d.setId(1L);
        d.setType(TYPE);
        d.setDictKey(key);
        d.setDictValue(value);
        d.setSort(sort);
        return d;
    }

    @Test
    @DisplayName("按类型查询字典项：正常返回列表")
    void listByType() {
        when(sysDictRepository.findByTypeOrderBySortAsc(TYPE))
                .thenReturn(List.of(dict("AVAILABLE", "空闲", 1)));
        assertEquals(1, sysDictService.listByType(TYPE).size());
    }

    @Test
    @DisplayName("按类型查询字典项：该类型下无数据时返回空列表")
    void listByTypeEmpty() {
        when(sysDictRepository.findByTypeOrderBySortAsc("NOT_EXIST")).thenReturn(List.of());
        assertEquals(0, sysDictService.listByType("NOT_EXIST").size());
    }

    @Test
    @DisplayName("按“类型+键”精确查询：命中时返回字典项")
    void findByTypeAndKeyHit() {
        when(sysDictRepository.findByTypeAndDictKey(TYPE, "AVAILABLE"))
                .thenReturn(Optional.of(dict("AVAILABLE", "空闲", 1)));
        assertEquals("空闲", sysDictService.findByTypeAndKey(TYPE, "AVAILABLE").getDictValue());
    }

    @Test
    @DisplayName("按“类型+键”精确查询：未命中时抛出业务异常")
    void findByTypeAndKeyMiss() {
        when(sysDictRepository.findByTypeAndDictKey(TYPE, "NOPE")).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> sysDictService.findByTypeAndKey(TYPE, "NOPE"));
        assertEquals("字典项不存在：" + TYPE + "/NOPE", ex.getMessage());
    }

    @Test
    @DisplayName("新增字典项：同类型下键重复时抛出业务异常")
    void createDuplicate() {
        when(sysDictRepository.existsByTypeAndDictKey(TYPE, "AVAILABLE")).thenReturn(true);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> sysDictService.create(dict("AVAILABLE", "空闲", 1)));
        assertEquals("该分类下的字典键已存在", ex.getMessage());
    }

    @Test
    @DisplayName("新增字典项：键不重复时正常保存")
    void createSuccess() {
        when(sysDictRepository.existsByTypeAndDictKey(TYPE, "NEW_KEY")).thenReturn(false);
        when(sysDictRepository.save(any(SysDict.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals("新值", sysDictService.create(dict("NEW_KEY", "新值", 9)).getDictValue());
    }

    @Test
    @DisplayName("修改字典项：字典值为 null 时抛出业务异常")
    void updateValueNull() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> sysDictService.updateValue(1L, null, 1));
        assertEquals("字典值不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("修改字典项：字典值为空白字符串时抛出业务异常")
    void updateValueBlank() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> sysDictService.updateValue(1L, "   ", 1));
        assertEquals("字典值不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("修改字典项：字典项不存在时抛出业务异常")
    void updateValueNotFound() {
        when(sysDictRepository.findById(404L)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> sysDictService.updateValue(404L, "新值", 1));
        assertEquals("记录不存在：404", ex.getMessage());
    }

    @Test
    @DisplayName("修改字典项：正常场景同时更新字典值与排序号")
    void updateValueSuccess() {
        when(sysDictRepository.findById(1L)).thenReturn(Optional.of(dict("AVAILABLE", "空闲", 1)));
        when(sysDictRepository.save(any(SysDict.class))).thenAnswer(inv -> inv.getArgument(0));
        SysDict updated = sysDictService.updateValue(1L, "可用", 5);
        assertEquals("可用", updated.getDictValue());
        assertEquals(5, updated.getSort().intValue());
    }
}
