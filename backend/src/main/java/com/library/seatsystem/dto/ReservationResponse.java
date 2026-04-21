package com.library.seatsystem.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
}

