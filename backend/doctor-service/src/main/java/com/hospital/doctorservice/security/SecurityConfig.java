package com.hospital.doctorservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        // Public/Read-only doctor & availability endpoints for all authenticated roles including PATIENT
                        .requestMatchers(HttpMethod.GET, "/api/doctors/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/doctors/*/availability").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/doctors/*/availability/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/doctors/*/availability/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/doctors/**").hasRole("ADMIN")
                        // Staff endpoints
                        .requestMatchers(HttpMethod.GET, "/api/staff/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/staff/*/schedules").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/staff/*/schedules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/staff/*/schedules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/staff/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/staff/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/staff/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
