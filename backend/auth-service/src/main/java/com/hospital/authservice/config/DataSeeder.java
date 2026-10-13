package com.hospital.authservice.config;

import com.hospital.authservice.entity.Role;
import com.hospital.authservice.entity.User;
import com.hospital.authservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDemoUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            seedUserIfNotExists(userRepository, passwordEncoder, "admin", "Admin User", "admin@hospital.com", "555-0101", Role.ADMIN);
            seedUserIfNotExists(userRepository, passwordEncoder, "doctor", "Dr. John Smith", "doctor@hospital.com", "555-0102", Role.DOCTOR);
            seedUserIfNotExists(userRepository, passwordEncoder, "doctor2", "Dr. Emily Clark", "emily.clark@hospital.com", "555-0150", Role.DOCTOR);
            seedUserIfNotExists(userRepository, passwordEncoder, "doctor3", "Dr. Robert Patel", "robert.patel@hospital.com", "555-0175", Role.DOCTOR);
            seedUserIfNotExists(userRepository, passwordEncoder, "staff", "Sarah Staff", "staff@hospital.com", "555-0103", Role.STAFF);
            seedUserIfNotExists(userRepository, passwordEncoder, "receptionist", "Rachel Receptionist", "receptionist@hospital.com", "555-0104", Role.RECEPTIONIST);
            seedUserIfNotExists(userRepository, passwordEncoder, "patient", "Peter Patient", "patient@hospital.com", "555-0105", Role.PATIENT);
        };
    }

    private void seedUserIfNotExists(UserRepository repo, PasswordEncoder encoder, String username, String fullName, String email, String phone, Role role) {
        if (!repo.existsByUsername(username)) {
            // Demo credentials: doctor accounts use doctor123; others use <username>123
            String rawPassword = username.startsWith("doctor") ? "doctor123" : username + "123";
            User user = new User(username, encoder.encode(rawPassword), fullName, email, phone, role);
            repo.save(user);
        }
    }
}
