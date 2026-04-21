package com.library.seatsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateStudyRoomRequest {

    @NotBlank(message = "自习室名称不能为空")
    private String roomName;

    @NotNull(message = "楼层不能为空")
    private Integer floor;

    @NotNull(message = "容量不能为空")
    private Integer capacity;
}

