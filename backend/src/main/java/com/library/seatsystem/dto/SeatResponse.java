package com.library.seatsystem.dto;

public class SeatResponse {

    private Long id;
    private Long roomId;
    private String roomName;
    private String seatCode;
    private String status;

    public SeatResponse(Long id, Long roomId, String roomName, String seatCode, String status) {
        this.id = id;
        this.roomId = roomId;
        this.roomName = roomName;
        this.seatCode = seatCode;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public String getStatus() {
        return status;
    }
}
