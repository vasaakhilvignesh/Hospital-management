package com.hospital.authservice.factory;

import com.hospital.authservice.entity.Role;

/**
 * Concrete Product for Staff / Doctor / Receptionist in Factory Method Pattern.
 */
public class StaffProfile implements UserProfile {
    private final String username;
    private final String fullName;
    private final String email;
    private final String phone;
    private final Role specificRole;

    public StaffProfile(String username, String fullName, String email, String phone, Role specificRole) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.specificRole = specificRole;
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
    public Role getRole() { return specificRole; }

    @Override
    public String getDepartmentOrDetails() { return "Clinical / Operations Staff: " + specificRole; }
}
