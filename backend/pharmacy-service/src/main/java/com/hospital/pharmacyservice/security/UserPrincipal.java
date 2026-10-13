package com.hospital.pharmacyservice.security;

public class UserPrincipal {
    private final Long userId;
    private final String username;
    private final String role;
    private final String rawToken;

    public UserPrincipal(Long userId, String username, String role, String rawToken) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.rawToken = rawToken;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getRawToken() { return rawToken; }
}
