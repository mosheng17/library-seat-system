package com.library.seatsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
/**
 * B 基础数据管理 —— 新增座位的请求参数。
 */
public class CreateSeatRequest {

    @NotNull(message = "自习室不能为空")
    private Long roomId;

    @NotBlank(message = "座位编号不能为空")
    private String seatCode;

    @NotBlank(message = "座位状态不能为空")
    private String status;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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
