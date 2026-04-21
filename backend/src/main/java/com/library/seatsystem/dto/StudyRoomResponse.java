package com.library.seatsystem.dto;

public class StudyRoomResponse {

    private Long id;
    private String roomName;
    private Integer floor;
    private Integer capacity;

    public StudyRoomResponse(Long id, String roomName, Integer floor, Integer capacity) {
        this.id = id;
        this.roomName = roomName;
        this.floor = floor;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public Integer getFloor() {
        return floor;
    }

    public Integer getCapacity() {
        return capacity;
    }
}
