package com.hospital.doctorservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.doctorservice.dto.*;
import com.hospital.doctorservice.entity.DoctorStatus;
import com.hospital.doctorservice.entity.Shift;
import com.hospital.doctorservice.entity.StaffStatus;
import com.hospital.doctorservice.repository.DoctorRepository;
import com.hospital.doctorservice.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DoctorServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private StaffRepository staffRepository;

    private String adminToken;
    private String doctorToken;
    private String patientToken;

    @BeforeEach
    void setUp() {
        adminToken = "Bearer " + generateRealToken(1L, "admin", "ADMIN");
        doctorToken = "Bearer " + generateRealToken(2L, "doctor", "DOCTOR");
        patientToken = "Bearer " + generateRealToken(5L, "patient", "PATIENT");
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
    void testDoctorCrudAndSearch() throws Exception {
        DoctorRequest req = new DoctorRequest(
                "DOC-9999",
                null,
                "Alexander",
                "Fleming",
                "alexander.fleming@hospital.com",
                "555-4343",
                "Microbiology",
                "Pathology",
                "MD, PhD",
                20,
                new BigDecimal("180.00"),
                DoctorStatus.ACTIVE
        );

        // Admin creates doctor
        MvcResult res = mockMvc.perform(post("/api/doctors")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.doctorCode").value("DOC-9999"))
                .andReturn();

        DoctorResponse created = objectMapper.readValue(res.getResponse().getContentAsString(), DoctorResponse.class);

        // Patient can view doctor
        mockMvc.perform(get("/api/doctors/" + created.getId())
                        .header("Authorization", patientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alexander"));

        // Search doctor
        mockMvc.perform(get("/api/doctors/search?query=Microbiology")
                        .header("Authorization", patientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].doctorCode").value("DOC-9999"));

        // Patient cannot update doctor -> 403 Forbidden
        req.setPhone("555-0000");
        mockMvc.perform(put("/api/doctors/" + created.getId())
                        .header("Authorization", patientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        // Admin updates doctor
        mockMvc.perform(put("/api/doctors/" + created.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("555-0000"));

        // Admin deletes doctor
        mockMvc.perform(delete("/api/doctors/" + created.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDoctorAvailabilityAndCheckEndpoint() throws Exception {
        var seededDoc = doctorRepository.findByDoctorCode("DOC-1001").orElseThrow();

        // Doctor adds new availability slot
        DoctorAvailabilityRequest avReq = new DoctorAvailabilityRequest(
                DayOfWeek.FRIDAY,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                true
        );

        mockMvc.perform(post("/api/doctors/" + seededDoc.getId() + "/availability")
                        .header("Authorization", doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(avReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dayOfWeek").value("FRIDAY"));

        // Patient checks availability on MONDAY 10:00 (seeded: 09:00 - 13:00) -> available: true
        mockMvc.perform(get("/api/doctors/" + seededDoc.getId() + "/availability/check?day=MONDAY&time=10:00")
                        .header("Authorization", patientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));

        // Patient checks availability on MONDAY 17:00 (outside slot) -> available: false
        mockMvc.perform(get("/api/doctors/" + seededDoc.getId() + "/availability/check?day=MONDAY&time=17:00")
                        .header("Authorization", patientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void testStaffAndScheduleFlow() throws Exception {
        StaffRequest staffReq = new StaffRequest(
                "STF-9999",
                null,
                "Mark",
                "Miller",
                "mark.miller@hospital.com",
                "555-7878",
                "Radiology Tech",
                "Imaging",
                LocalDate.now(),
                StaffStatus.ACTIVE
        );

        // Patient cannot create staff -> 403 Forbidden
        mockMvc.perform(post("/api/staff")
                        .header("Authorization", patientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staffReq)))
                .andExpect(status().isForbidden());

        // Admin creates staff
        MvcResult res = mockMvc.perform(post("/api/staff")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staffReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.staffCode").value("STF-9999"))
                .andReturn();

        StaffResponse created = objectMapper.readValue(res.getResponse().getContentAsString(), StaffResponse.class);

        // Admin adds staff schedule
        StaffScheduleRequest schedReq = new StaffScheduleRequest(
                DayOfWeek.THURSDAY,
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                Shift.MORNING
        );

        mockMvc.perform(post("/api/staff/" + created.getId() + "/schedules")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(schedReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shift").value("MORNING"));

        // Doctor can view staff schedules
        mockMvc.perform(get("/api/staff/" + created.getId() + "/schedules")
                        .header("Authorization", doctorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
