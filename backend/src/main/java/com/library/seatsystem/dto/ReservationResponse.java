package com.library.seatsystem.dto;

import java.time.LocalDateTime;
public class ReservationResponse {

    private Long id;
    private Long userId;
    private String realName;
    private Long seatId;
    private String seatCode;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public ReservationResponse(
            Long id,
            Long userId,
            String realName,
            Long seatId,
            String seatCode,
            String roomName,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String status
    ) {
        this.id = id;
        this.userId = userId;
        this.realName = realName;
        this.seatId = seatId;
        this.seatCode = seatCode;
        this.roomName = roomName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRealName() {
        return realName;
    }

    public Long getSeatId() {
        return seatId;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }
}
