package com.library.seatsystem.controller;

import com.library.seatsystem.entity.Seat;
import com.library.seatsystem.service.SeatService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/room/{roomId}")
    public List<Seat> getSeatsByRoom(@PathVariable Long roomId) {
        return seatService.getSeatsByRoom(roomId);
    }
}

