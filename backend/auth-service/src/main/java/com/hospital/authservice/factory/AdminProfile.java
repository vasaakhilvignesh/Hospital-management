package com.hospital.authservice.factory;

import com.hospital.authservice.entity.Role;

/**
 * Concrete Product for Admin in Factory Method Pattern.
 */
public class AdminProfile implements UserProfile {
    private final String username;
    private final String fullName;
    private final String email;
    private final String phone;

    public AdminProfile(String username, String fullName, String email, String phone) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public String getUsername() { return username; }

    @Override
    public String getFullName() { return fullName; }

    @Override
    public String getEmail() { return email; }

    @Override
    public String getPhone() { return phone; }

    @Override
    public Role getRole() { return Role.ADMIN; }

    @Override
    public String getDepartmentOrDetails() { return "Hospital Administration"; }
}
