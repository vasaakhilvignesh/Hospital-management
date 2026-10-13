package com.hospital.authservice.factory;

import com.hospital.authservice.dto.RegisterRequest;
import org.springframework.stereotype.Component;

/**
 * Concrete Creator for Patient users.
 */
@Component
public class PatientFactory extends UserFactory {

    @Override
    public UserProfile createUserProfile(RegisterRequest request) {
        return new PatientProfile(
                request.getUsername(),
                request.getFullName(),
                request.getEmail(),
                request.getPhone()
        );
    }
}
