package com.hospital.patientservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.patientservice.dto.ImmunizationRequest;
import com.hospital.patientservice.dto.MedicalHistoryRequest;
import com.hospital.patientservice.dto.PatientRequest;
import com.hospital.patientservice.dto.PatientResponse;
import com.hospital.patientservice.repository.PatientRepository;
import com.hospital.patientservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PatientServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String receptionistToken;
    private String doctorToken;
    private String patientToken;
    private String otherPatientToken;

    @BeforeEach
    void setUp() {
        adminToken = "Bearer " + generateRealToken(1L, "admin", "ADMIN");
        receptionistToken = "Bearer " + generateRealToken(4L, "receptionist", "RECEPTIONIST");
        doctorToken = "Bearer " + generateRealToken(2L, "doctor", "DOCTOR");
        patientToken = "Bearer " + generateRealToken(5L, "patient", "PATIENT"); // linked to Peter Patient (seeded)
        otherPatientToken = "Bearer " + generateRealToken(99L, "otherpatient", "PATIENT");
    }

    private String generateRealToken(Long userId, String username, String role) {
        return io.jsonwebtoken.Jwts.builder()
                .claims(java.util.Map.of("userId", userId, "role", role))
                .subject(username)
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + 3600000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970".getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    void testCreateRetrieveAndUpdatePatient() throws Exception {
        PatientRequest request = new PatientRequest(
                "PAT-9999",
                100L,
                "Robert",
                "Williams",
                LocalDate.of(1988, 3, 10),
                "Male",
                "555-4321",
                "robert.williams@test.com",
                "789 Pine Rd",
                "B+",
                "Jane Williams"
        );

        // Receptionist can create patient
        MvcResult createRes = mockMvc.perform(post("/api/patients")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientCode").value("PAT-9999"))
                .andReturn();

        PatientResponse created = objectMapper.readValue(createRes.getResponse().getContentAsString(), PatientResponse.class);

        // Doctor can retrieve patient by ID
        mockMvc.perform(get("/api/patients/" + created.getId())
                        .header("Authorization", doctorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Robert"));

        // Doctor can search patient
        mockMvc.perform(get("/api/patients/search?query=Williams")
                        .header("Authorization", doctorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientCode").value("PAT-9999"));

        // Update patient
        request.setPhone("555-8888");
        mockMvc.perform(put("/api/patients/" + created.getId())
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("555-8888"));

        // Duplicate code rejection
        mockMvc.perform(post("/api/patients")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        // Admin can delete patient
        mockMvc.perform(delete("/api/patients/" + created.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testPatientIsolationRbac() throws Exception {
        // Patient cannot create patients -> 403
        PatientRequest request = new PatientRequest(null, null, "Hacker", "User", null, null, null, null, null, null, null);
        mockMvc.perform(post("/api/patients")
                        .header("Authorization", patientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // Patient with userId 5 can access own record (Peter Patient is seeded with userId=5)
        var seeded = patientRepository.findByPatientCode("PAT-1001").orElseThrow();
        mockMvc.perform(get("/api/patients/" + seeded.getId())
                        .header("Authorization", patientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Peter"));

        // Another patient (userId 99) attempting to access Peter's record -> 403 Forbidden
        mockMvc.perform(get("/api/patients/" + seeded.getId())
                        .header("Authorization", otherPatientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMedicalHistoryAndImmunizationFlow() throws Exception {
        var seeded = patientRepository.findByPatientCode("PAT-1001").orElseThrow();

        // Doctor adds medical history
        MedicalHistoryRequest historyReq = new MedicalHistoryRequest(
                "Acute Bronchitis",
                "Antibiotics 500mg daily",
                "Amoxicillin",
                "Follow up in 7 days",
                LocalDate.now(),
                "Dr. John Smith"
        );

        MvcResult histResult = mockMvc.perform(post("/api/patients/" + seeded.getId() + "/history")
                        .header("Authorization", doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(historyReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.diagnosis").value("Acute Bronchitis"))
                .andReturn();

        // Patient can view own medical history
        mockMvc.perform(get("/api/patients/" + seeded.getId() + "/history")
                        .header("Authorization", patientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Receptionist adds immunization
        ImmunizationRequest immReq = new ImmunizationRequest(
                "Hepatitis B",
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(5),
                "Dose 1 administered"
        );

        mockMvc.perform(post("/api/patients/" + seeded.getId() + "/immunizations")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(immReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vaccineName").value("Hepatitis B"));

        // Patient can view own immunizations
        mockMvc.perform(get("/api/patients/" + seeded.getId() + "/immunizations")
                        .header("Authorization", patientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
