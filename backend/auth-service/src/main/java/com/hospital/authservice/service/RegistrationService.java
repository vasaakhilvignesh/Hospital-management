package com.hospital.authservice.service;

import com.hospital.authservice.dto.RegisterRequest;
import com.hospital.authservice.dto.UserResponse;
import com.hospital.authservice.entity.User;
import com.hospital.authservice.exception.UserAlreadyExistsException;
import com.hospital.authservice.factory.UserFactory;
import com.hospital.authservice.factory.UserFactoryProvider;
import com.hospital.authservice.factory.UserProfile;
import com.hospital.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RegistrationService acts as the CLIENT in the Factory Method Pattern.
 * Rather than instantiating role-specific domain models directly with "new",
 * it requests the appropriate Creator via UserFactoryProvider and delegates object creation.
 */
@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFactoryProvider userFactoryProvider;

    public RegistrationService(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               UserFactoryProvider userFactoryProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userFactoryProvider = userFactoryProvider;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Validate uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
        }

        // =========================================================================
        // FACTORY METHOD PATTERN USAGE:
        // 1. Resolve Creator (AdminFactory, PatientFactory, or StaffFactory)
        // 2. Invoke Factory Method 'createUserProfile(request)'
        // Client does not directly couple itself to concrete product constructors.
        // =========================================================================
        UserFactory factory = userFactoryProvider.getFactory(request.getRole());
        UserProfile userProfile = factory.createUserProfile(request);

        // Build persisted entity with hashed password
        User user = new User(
                userProfile.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                userProfile.getFullName(),
                userProfile.getEmail(),
                userProfile.getPhone(),
                userProfile.getRole()
        );

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }
}
