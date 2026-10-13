package com.hospital.authservice.factory;

import com.hospital.authservice.entity.Role;

/**
 * Concrete Product for Patient in Factory Method Pattern.
 */
public class PatientProfile implements UserProfile {
    private final String username;
    private final String fullName;
    private final String email;
    private final String phone;

    public PatientProfile(String username, String fullName, String email, String phone) {
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
    public Role getRole() { return Role.PATIENT; }

    @Override
    public String getDepartmentOrDetails() { return "Outpatient/Inpatient Care"; }
}
