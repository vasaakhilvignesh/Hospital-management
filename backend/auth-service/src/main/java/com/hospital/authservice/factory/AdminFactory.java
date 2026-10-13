package com.hospital.authservice.factory;

import com.hospital.authservice.dto.RegisterRequest;
import org.springframework.stereotype.Component;

/**
 * Concrete Creator for Admin users.
 */
@Component
public class AdminFactory extends UserFactory {

    @Override
    public UserProfile createUserProfile(RegisterRequest request) {
        return new AdminProfile(
                request.getUsername(),
                request.getFullName(),
                request.getEmail(),
                request.getPhone()
        );
    }
}
