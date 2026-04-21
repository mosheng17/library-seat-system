package com.library.seatsystem.dto;

public class LoginResponse {

    private Long userId;
    private String username;
    private String realName;
    private String role;

    public LoginResponse(Long userId, String username, String realName, String role) {
        this.userId = userId;
        this.username = username;
        this.realName = realName;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRealName() {
        return realName;
    }

    public String getRole() {
        return role;
    }
}
