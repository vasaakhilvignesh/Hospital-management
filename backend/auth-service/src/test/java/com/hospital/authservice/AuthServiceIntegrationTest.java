package com.hospital.authservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.authservice.dto.LoginRequest;
import com.hospital.authservice.dto.LoginResponse;
import com.hospital.authservice.dto.RegisterRequest;
import com.hospital.authservice.entity.Role;
import com.hospital.authservice.factory.*;
import com.hospital.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserFactoryProvider userFactoryProvider;

    @Test
    void testFactoryMethodImplementation() {
        // Test Creator resolution and polymorphic Product creation
        UserFactory adminFactory = userFactoryProvider.getFactory(Role.ADMIN);
        assertTrue(adminFactory instanceof AdminFactory);
        UserProfile adminProfile = adminFactory.createUserProfile(new RegisterRequest("adm", "pass", "Adm Name", "adm@test.com", "123", Role.ADMIN));
        assertTrue(adminProfile instanceof AdminProfile);
        assertEquals(Role.ADMIN, adminProfile.getRole());

        UserFactory patientFactory = userFactoryProvider.getFactory(Role.PATIENT);
        assertTrue(patientFactory instanceof PatientFactory);
        UserProfile patientProfile = patientFactory.createUserProfile(new RegisterRequest("pat", "pass", "Pat Name", "pat@test.com", "123", Role.PATIENT));
        assertTrue(patientProfile instanceof PatientProfile);
        assertEquals(Role.PATIENT, patientProfile.getRole());

        UserFactory staffFactory = userFactoryProvider.getFactory(Role.DOCTOR);
        assertTrue(staffFactory instanceof StaffFactory);
        UserProfile docProfile = staffFactory.createUserProfile(new RegisterRequest("doc", "pass", "Doc Name", "doc@test.com", "123", Role.DOCTOR));
        assertTrue(docProfile instanceof StaffProfile);
        assertEquals(Role.DOCTOR, docProfile.getRole());
    }

    @Test
    void testRegistrationSuccessAndDuplicateRejection() throws Exception {
        RegisterRequest req = new RegisterRequest("newpatient", "password123", "New Patient", "newpatient@hospital.com", "555-9999", Role.PATIENT);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newpatient"))
                .andExpect(jsonPath("$.role").value("PATIENT"));

        // Verify password hashing in DB
        var user = userRepository.findByUsername("newpatient").orElseThrow();
        assertTrue(passwordEncoder.matches("password123", user.getPassword()));
        assertNotEquals("password123", user.getPassword());

        // Attempt duplicate registration -> Conflict (409)
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void testLoginSuccessAndInvalidCredentials() throws Exception {
        LoginRequest validReq = new LoginRequest("admin", "admin123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        LoginResponse resp = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
        assertNotNull(resp.getToken());

        // Invalid password -> Unauthorized (401)
        LoginRequest invalidReq = new LoginRequest("admin", "wrongpass");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedEndpointAndRbac() throws Exception {
        // Without token -> Forbidden (403)
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());

        // Obtain Admin token
        MvcResult adminLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin", "admin123"))))
                .andReturn();
        String adminToken = objectMapper.readValue(adminLogin.getResponse().getContentAsString(), LoginResponse.class).getToken();

        // Obtain Patient token
        MvcResult patientLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("patient", "patient123"))))
                .andReturn();
        String patientToken = objectMapper.readValue(patientLogin.getResponse().getContentAsString(), LoginResponse.class).getToken();

        // Admin token on /api/auth/me -> 200 OK
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));

        // Admin token on /api/auth/admin-test -> 200 OK
        mockMvc.perform(get("/api/auth/admin-test")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Patient token on /api/auth/admin-test -> 403 Forbidden
        mockMvc.perform(get("/api/auth/admin-test")
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isForbidden());

        // Patient token on /api/auth/patient-test -> 200 OK
        mockMvc.perform(get("/api/auth/patient-test")
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isOk());
    }
}
