package com.library.seatsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
/**
 * B 基础数据管理 —— 新增自习室的请求参数。
 */
public class CreateStudyRoomRequest {

    @NotBlank(message = "自习室名称不能为空")
    private String roomName;

    @NotNull(message = "楼层不能为空")
    private Integer floor;

    @NotNull(message = "容量不能为空")
    private Integer capacity;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
