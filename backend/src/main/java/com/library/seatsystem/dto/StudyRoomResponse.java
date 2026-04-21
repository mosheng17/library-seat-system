package com.library.seatsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudyRoomResponse {

    private Long id;
    private String roomName;
    private Integer floor;
    private Integer capacity;
}

