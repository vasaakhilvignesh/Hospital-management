package com.hospital.authservice.factory;

import com.hospital.authservice.dto.RegisterRequest;
import org.springframework.stereotype.Component;

/**
 * Concrete Creator for Staff users (Staff, Doctor, Receptionist).
 */
@Component
public class StaffFactory extends UserFactory {

    @Override
    public UserProfile createUserProfile(RegisterRequest request) {
        return new StaffProfile(
                request.getUsername(),
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                request.getRole()
        );
    }
}
