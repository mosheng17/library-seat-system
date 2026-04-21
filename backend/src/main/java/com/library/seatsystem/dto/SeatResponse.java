package com.library.seatsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatResponse {

    private Long id;
    private Long roomId;
    private String roomName;
    private String seatCode;
    private String status;
}

