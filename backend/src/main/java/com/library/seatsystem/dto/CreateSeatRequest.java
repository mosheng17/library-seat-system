package com.library.seatsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSeatRequest {

    @NotNull(message = "自习室不能为空")
    private Long roomId;

    @NotBlank(message = "座位编号不能为空")
    private String seatCode;

    @NotBlank(message = "座位状态不能为空")
    private String status;
}

