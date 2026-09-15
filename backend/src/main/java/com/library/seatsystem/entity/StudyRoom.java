package com.library.seatsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * B 基础数据管理 —— 自习室实体（study_rooms 表）。
 *
 * <p>记录自习室名称、所在楼层与容量，容量用于估算使用率分母。
 */
@Entity
@Table(name = "study_rooms")
public class StudyRoom extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
/** 自习室名称（唯一） */
    private String roomName;

    @Column(nullable = false)
/** 所在楼层 */
    private Integer floor;

    @Column(nullable = false)
/** 座位数，用于估算使用率分母 */
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
