package com.library.seatsystem.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReservationRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long seatId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;
}

