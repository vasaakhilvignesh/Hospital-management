package com.hospital.authservice.service;

import com.hospital.authservice.dto.LoginRequest;
import com.hospital.authservice.dto.LoginResponse;
import com.hospital.authservice.dto.UserResponse;
import com.hospital.authservice.entity.User;
import com.hospital.authservice.exception.InvalidCredentialsException;
import com.hospital.authservice.exception.UserNotFoundException;
import com.hospital.authservice.repository.UserRepository;
import com.hospital.authservice.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + request.getUsername()));

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole()
        );
    }

    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        return UserResponse.fromEntity(user);
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }
}
