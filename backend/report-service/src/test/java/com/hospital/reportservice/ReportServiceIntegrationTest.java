package com.hospital.reportservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.reportservice.client.*;
import com.hospital.reportservice.dto.ReportRequest;
import com.hospital.reportservice.dto.ReportResponse;
import com.hospital.reportservice.entity.ReportStatus;
import com.hospital.reportservice.entity.ReportType;
import com.hospital.reportservice.repository.ReportRepository;
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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "eureka.client.enabled=false"
})
@AutoConfigureMockMvc
class ReportServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportRepository reportRepository;

    @MockBean
    private PatientServiceClient patientServiceClient;

    @MockBean
    private DoctorServiceClient doctorServiceClient;

    @MockBean
    private AppointmentServiceClient appointmentServiceClient;

    @MockBean
    private BillingServiceClient billingServiceClient;

    @MockBean
    private PharmacyServiceClient pharmacyServiceClient;

    private String adminToken;
    private String doctorToken;
    private String patientToken;
    private String otherPatientToken;

    @BeforeEach
    void setUp() {
        adminToken = "Bearer " + generateRealToken(1L, "admin", "ADMIN");
        doctorToken = "Bearer " + generateRealToken(2L, "doctor", "DOCTOR");
        patientToken = "Bearer " + generateRealToken(1L, "patient", "PATIENT");
        otherPatientToken = "Bearer " + generateRealToken(99L, "otherpatient", "PATIENT");

        // Mock downstream REST responses
        Mockito.when(patientServiceClient.getPatient(eq(1L), anyString()))
                .thenReturn(Map.of("id", 1L, "userId", 1L, "firstName", "Peter", "lastName", "Patient"));
        Mockito.when(patientServiceClient.getAllPatients(anyString()))
                .thenReturn(List.of(Map.of("id", 1L), Map.of("id", 2L)));
        Mockito.when(patientServiceClient.getMedicalHistories(eq(1L), anyString()))
                .thenReturn(List.of(Map.of("diagnosis", "Hypertension")));

        Mockito.when(doctorServiceClient.getAllDoctors(anyString()))
                .thenReturn(List.of(Map.of("id", 1L), Map.of("id", 2L), Map.of("id", 3L)));

        Mockito.when(appointmentServiceClient.getAllAppointments(anyString()))
                .thenReturn(List.of(
                        Map.of("id", 1L, "status", "SCHEDULED"),
                        Map.of("id", 2L, "status", "COMPLETED"),
                        Map.of("id", 3L, "status", "CONFIRMED")
                ));
        Mockito.when(appointmentServiceClient.getAppointmentsByPatient(eq(1L), anyString()))
                .thenReturn(List.of(
                        Map.of("id", 1L, "status", "SCHEDULED"),
                        Map.of("id", 2L, "status", "COMPLETED")
                ));

        Mockito.when(billingServiceClient.getAllBills(anyString()))
                .thenReturn(List.of(
                        Map.of("id", 1L, "balanceAmount", 50.00),
                        Map.of("id", 2L, "balanceAmount", 150.00)
                ));
        Mockito.when(billingServiceClient.getBillsByPatient(eq(1L), anyString()))
                .thenReturn(List.of(
                        Map.of("id", 1L, "totalAmount", 100.00, "paidAmount", 50.00, "balanceAmount", 50.00)
                ));

        Mockito.when(pharmacyServiceClient.getAllPrescriptions(anyString()))
                .thenReturn(List.of(
                        Map.of("id", 1L, "status", "PENDING"),
                        Map.of("id", 2L, "status", "DISPENSED")
                ));
        Mockito.when(pharmacyServiceClient.getPrescriptionsByPatient(eq(1L), anyString()))
                .thenReturn(List.of(
                        Map.of("id", 1L, "status", "PENDING")
                ));
        Mockito.when(pharmacyServiceClient.getLowStock(anyString()))
                .thenReturn(List.of(Map.of("batchNumber", "BATCH-IBU-001")));
    }

    private String generateRealToken(Long userId, String username, String role) {
        return io.jsonwebtoken.Jwts.builder()
                .claims(Map.of("userId", userId, "role", role))
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970".getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    void testReportCreationAndSummaries() throws Exception {
        // 1. Create Medical Summary
        ReportRequest medReq = new ReportRequest(1L, ReportType.MEDICAL_SUMMARY, "Health Overview", null, null);
        mockMvc.perform(post("/api/reports")
                        .header("Authorization", doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportCode").isNotEmpty())
                .andExpect(jsonPath("$.reportType").value("MEDICAL_SUMMARY"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));

        // 2. Create Billing Summary
        ReportRequest billReq = new ReportRequest(1L, ReportType.BILLING_SUMMARY, "Finance Audit", null, null);
        mockMvc.perform(post("/api/reports")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(billReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportType").value("BILLING_SUMMARY"));

        // 3. Create Appointment Summary
        ReportRequest aptReq = new ReportRequest(1L, ReportType.APPOINTMENT_SUMMARY, "Visit Log", null, null);
        mockMvc.perform(post("/api/reports")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aptReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportType").value("APPOINTMENT_SUMMARY"));
    }

    @Test
    void testDashboardSummary() throws Exception {
        mockMvc.perform(get("/api/reports/dashboard/summary")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPatients").value(2))
                .andExpect(jsonPath("$.totalDoctors").value(3))
                .andExpect(jsonPath("$.totalAppointments").value(3))
                .andExpect(jsonPath("$.pendingAppointments").value(2))
                .andExpect(jsonPath("$.completedAppointments").value(1))
                .andExpect(jsonPath("$.outstandingBilling").value(200.0))
                .andExpect(jsonPath("$.pendingPrescriptions").value(1))
                .andExpect(jsonPath("$.lowStockMedicineCount").value(1));
    }

    @Test
    void testPatientReportIsolationAndArchive() throws Exception {
        ReportRequest req = new ReportRequest(1L, ReportType.GENERAL, "Personal Summary", "Notes", "{}");
        MvcResult res = mockMvc.perform(post("/api/reports")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        ReportResponse created = objectMapper.readValue(res.getResponse().getContentAsString(), ReportResponse.class);

        // Patient 1 can view own report
        mockMvc.perform(get("/api/reports/" + created.getId())
                        .header("Authorization", patientToken))
                .andExpect(status().isOk());

        // Other patient cannot view Patient 1's report -> 403 Forbidden
        mockMvc.perform(get("/api/reports/" + created.getId())
                        .header("Authorization", otherPatientToken))
                .andExpect(status().isForbidden());

        // Archive report
        mockMvc.perform(patch("/api/reports/" + created.getId() + "/archive")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));
    }
}
