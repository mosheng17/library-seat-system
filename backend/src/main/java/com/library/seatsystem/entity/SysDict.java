package com.library.seatsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * B 基础数据管理 —— 数据字典表（sys_dict）。
 *
 * <p>用于维护"座位状态""预约状态""角色"等分类/字典项。
 */
@Entity
@Table(name = "sys_dict")
public class SysDict extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "dict_key", nullable = false, length = 50)
    private String dictKey;

    @Column(name = "dict_value", nullable = false, length = 100)
    private String dictValue;

    @Column(name = "sort_no")
    private Integer sort;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDictKey() {
        return dictKey;
    }

    public void setDictKey(String dictKey) {
        this.dictKey = dictKey;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
