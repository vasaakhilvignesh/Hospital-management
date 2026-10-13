package com.hospital.appointmentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.appointmentservice.client.DoctorServiceClient;
import com.hospital.appointmentservice.client.PatientServiceClient;
import com.hospital.appointmentservice.dto.AppointmentRequest;
import com.hospital.appointmentservice.dto.AppointmentRescheduleRequest;
import com.hospital.appointmentservice.dto.AppointmentResponse;
import com.hospital.appointmentservice.entity.AppointmentStatus;
import com.hospital.appointmentservice.exception.DoctorNotFoundException;
import com.hospital.appointmentservice.exception.PatientNotFoundException;
import com.hospital.appointmentservice.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AppointmentServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @MockBean
    private PatientServiceClient patientServiceClient;

    @MockBean
    private DoctorServiceClient doctorServiceClient;

    private String adminToken;
    private String receptionistToken;
    private String patientToken;
    private String otherPatientToken;

    @BeforeEach
    void setUp() {
        adminToken = "Bearer " + generateRealToken(1L, "admin", "ADMIN");
        receptionistToken = "Bearer " + generateRealToken(4L, "receptionist", "RECEPTIONIST");
        patientToken = "Bearer " + generateRealToken(5L, "patient", "PATIENT");
        otherPatientToken = "Bearer " + generateRealToken(99L, "otherpatient", "PATIENT");

        // Mock Patient Service responses
        Mockito.when(patientServiceClient.getPatient(eq(1L), anyString()))
                .thenReturn(Map.of("id", 1L, "userId", 5L, "firstName", "Peter", "lastName", "Patient"));
        Mockito.when(patientServiceClient.getPatient(eq(2L), anyString()))
                .thenReturn(Map.of("id", 2L, "userId", 100L, "firstName", "Alice", "lastName", "Johnson"));
        Mockito.when(patientServiceClient.getPatient(eq(999L), anyString()))
                .thenThrow(new PatientNotFoundException("Patient not found with id: 999"));

        // Mock Doctor Service responses
        Mockito.when(doctorServiceClient.getDoctor(eq(1L), anyString()))
                .thenReturn(Map.of("id", 1L, "firstName", "John", "lastName", "Smith", "specialization", "Cardiology"));
        Mockito.when(doctorServiceClient.getDoctor(eq(999L), anyString()))
                .thenThrow(new DoctorNotFoundException("Doctor not found with id: 999"));

        // Default doctor availability: Monday 09:00 - 13:00 is available
        Mockito.when(doctorServiceClient.checkAvailability(eq(1L), eq(DayOfWeek.MONDAY), eq(LocalTime.of(10, 0)), anyString()))
                .thenReturn(true);
        Mockito.when(doctorServiceClient.checkAvailability(eq(1L), eq(DayOfWeek.MONDAY), eq(LocalTime.of(10, 15)), anyString()))
                .thenReturn(true);
        Mockito.when(doctorServiceClient.checkAvailability(eq(1L), eq(DayOfWeek.MONDAY), eq(LocalTime.of(11, 0)), anyString()))
                .thenReturn(true);
        Mockito.when(doctorServiceClient.checkAvailability(eq(1L), eq(DayOfWeek.SUNDAY), any(), anyString()))
                .thenReturn(false);
    }

    private String generateRealToken(Long userId, String username, String role) {
        return io.jsonwebtoken.Jwts.builder()
                .claims(Map.of("userId", userId, "role", role))
                .subject(username)
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + 3600000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970".getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    void testCreateAppointmentWithPatientAndDoctorValidation() throws Exception {
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        // 1. Valid appointment creation by Receptionist
        AppointmentRequest req = new AppointmentRequest(
                1L,
                1L,
                nextMonday,
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                "Routine checkup",
                "None"
        );

        MvcResult res = mockMvc.perform(post("/api/appointments")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentCode").isNotEmpty())
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andReturn();

        AppointmentResponse created = objectMapper.readValue(res.getResponse().getContentAsString(), AppointmentResponse.class);

        // 2. Reject appointment creation with non-existent patient (Patient Service 404)
        AppointmentRequest invalidPatReq = new AppointmentRequest(
                999L,
                1L,
                nextMonday,
                LocalTime.of(11, 0),
                LocalTime.of(11, 30),
                "Check",
                null
        );

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatReq)))
                .andExpect(status().isNotFound());

        // 3. Reject appointment creation with non-existent doctor (Doctor Service 404)
        AppointmentRequest invalidDocReq = new AppointmentRequest(
                1L,
                999L,
                nextMonday,
                LocalTime.of(11, 0),
                LocalTime.of(11, 30),
                "Check",
                null
        );

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDocReq)))
                .andExpect(status().isNotFound());

        // 4. Reject appointment when doctor is unavailable (Sunday)
        LocalDate nextSunday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        AppointmentRequest unavailReq = new AppointmentRequest(
                1L,
                1L,
                nextSunday,
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                "Check",
                null
        );

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unavailReq)))
                .andExpect(status().isBadRequest());

        // 5. Overlapping conflict rejection (same doctor, overlapping slot: 10:15 - 10:45 overlaps with 10:00 - 10:30)
        AppointmentRequest conflictReq = new AppointmentRequest(
                2L,
                1L,
                nextMonday,
                LocalTime.of(10, 15),
                LocalTime.of(10, 45),
                "Conflicting check",
                null
        );

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflictReq)))
                .andExpect(status().isConflict());

        // 6. Reschedule appointment
        AppointmentRescheduleRequest reschedReq = new AppointmentRescheduleRequest(
                nextMonday,
                LocalTime.of(11, 0),
                LocalTime.of(11, 30)
        );

        mockMvc.perform(put("/api/appointments/" + created.getId() + "/reschedule")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reschedReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESCHEDULED"));

        // 7. Cancel appointment
        mockMvc.perform(patch("/api/appointments/" + created.getId() + "/cancel")
                        .header("Authorization", receptionistToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void testPatientAppointmentIsolation() throws Exception {
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        // Peter Patient (patientId 1, userId 5) can view own appointments
        mockMvc.perform(get("/api/appointments/patient/1")
                        .header("Authorization", patientToken))
                .andExpect(status().isOk());

        // Peter Patient attempting to view Alice's appointments (patientId 2, userId 100) -> 403 Forbidden
        mockMvc.perform(get("/api/appointments/patient/2")
                        .header("Authorization", patientToken))
                .andExpect(status().isForbidden());

        // Other patient user trying to book under Peter's patientId 1 -> 403 Forbidden
        AppointmentRequest req = new AppointmentRequest(
                1L,
                1L,
                nextMonday,
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                "Malicious book",
                null
        );

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", otherPatientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }
}
