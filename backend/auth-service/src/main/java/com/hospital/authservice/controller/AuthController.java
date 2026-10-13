package com.hospital.authservice.controller;

import com.hospital.authservice.dto.LoginRequest;
import com.hospital.authservice.dto.LoginResponse;
import com.hospital.authservice.dto.RegisterRequest;
import com.hospital.authservice.dto.UserResponse;
import com.hospital.authservice.service.AuthService;
import com.hospital.authservice.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthService authService;

    public AuthController(RegistrationService registrationService, AuthService authService) {
        this.registrationService = registrationService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request, Authentication authentication) {
        // Enforce privilege security: non-ADMIN callers can only register as PATIENT
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            request.setRole(com.hospital.authservice.entity.Role.PATIENT);
        }
        UserResponse response = registrationService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserResponse response = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(response);
    }

    // Role-verification demonstration endpoints
    @GetMapping("/admin-test")
    public ResponseEntity<Map<String, String>> adminTest(Authentication authentication) {
        return ResponseEntity.ok(Map.of("message", "Admin access granted", "user", authentication.getName()));
    }

    @GetMapping("/doctor-test")
    public ResponseEntity<Map<String, String>> doctorTest(Authentication authentication) {
        return ResponseEntity.ok(Map.of("message", "Doctor access granted", "user", authentication.getName()));
    }

    @GetMapping("/patient-test")
    public ResponseEntity<Map<String, String>> patientTest(Authentication authentication) {
        return ResponseEntity.ok(Map.of("message", "Patient access granted", "user", authentication.getName()));
    }

    @GetMapping("/staff-test")
    public ResponseEntity<Map<String, String>> staffTest(Authentication authentication) {
        return ResponseEntity.ok(Map.of("message", "Staff access granted", "user", authentication.getName()));
    }

    @GetMapping("/receptionist-test")
    public ResponseEntity<Map<String, String>> receptionistTest(Authentication authentication) {
        return ResponseEntity.ok(Map.of("message", "Receptionist access granted", "user", authentication.getName()));
    }
}
