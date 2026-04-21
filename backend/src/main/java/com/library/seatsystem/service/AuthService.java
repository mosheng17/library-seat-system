package com.library.seatsystem.service;

import com.library.seatsystem.dto.LoginRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String login(LoginRequest request) {
        return "TODO: implement login for user " + request.getUsername();
    }
}

