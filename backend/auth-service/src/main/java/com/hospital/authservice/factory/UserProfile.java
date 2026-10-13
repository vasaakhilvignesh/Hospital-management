package com.hospital.authservice.factory;

import com.hospital.authservice.entity.Role;

/**
 * Product Interface in Factory Method Pattern.
 * Defines the contract for user profiles created across different user roles.
 */
public interface UserProfile {
    String getUsername();
    String getFullName();
    String getEmail();
    String getPhone();
    Role getRole();
    String getDepartmentOrDetails();
}
