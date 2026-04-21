package com.library.seatsystem.service;

import com.library.seatsystem.dto.ReservationRequest;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    public String createReservation(ReservationRequest request) {
        return "TODO: create reservation for seat " + request.getSeatId();
    }

    public String cancelReservation(Long reservationId) {
        return "TODO: cancel reservation " + reservationId;
    }
}

