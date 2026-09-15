package com.library.seatsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * B 基础数据管理 —— 座位实体（seats 表）。
 *
 * <p>座位归属某个自习室，状态取值见 {@link com.library.seatsystem.common.BizConstants}。
 */
@Entity
@Table(name = "seats")
public class Seat extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
/** 所属自习室 */
    private StudyRoom studyRoom;

    @Column(nullable = false, length = 20)
/** 座位编号（同室内唯一） */
    private String seatCode;

    @Column(nullable = false, length = 20)
/** 座位状态，取值见 BizConstants */
    private String status;

    public StudyRoom getStudyRoom() {
        return studyRoom;
    }

    public void setStudyRoom(StudyRoom studyRoom) {
        this.studyRoom = studyRoom;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
