package com.hospital.billingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.billingservice.client.AppointmentServiceClient;
import com.hospital.billingservice.client.PatientServiceClient;
import com.hospital.billingservice.client.PharmacyServiceClient;
import com.hospital.billingservice.dto.*;
import com.hospital.billingservice.entity.BillStatus;
import com.hospital.billingservice.entity.ItemType;
import com.hospital.billingservice.entity.PaymentMethod;
import com.hospital.billingservice.entity.PaymentStatus;
import com.hospital.billingservice.exception.PatientNotFoundException;
import com.hospital.billingservice.repository.BillRepository;
import com.hospital.billingservice.repository.InsuranceRepository;
import com.hospital.billingservice.repository.PaymentRepository;
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
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BillingServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @MockBean
    private PatientServiceClient patientServiceClient;

    @MockBean
    private AppointmentServiceClient appointmentServiceClient;

    @MockBean
    private PharmacyServiceClient pharmacyServiceClient;

    private String adminToken;
    private String receptionistToken;
    private String patientToken;
    private String otherPatientToken;

    @BeforeEach
    void setUp() {
        adminToken = "Bearer " + generateRealToken(1L, "admin", "ADMIN");
        receptionistToken = "Bearer " + generateRealToken(4L, "receptionist", "RECEPTIONIST");
        patientToken = "Bearer " + generateRealToken(1L, "patient", "PATIENT");
        otherPatientToken = "Bearer " + generateRealToken(99L, "otherpatient", "PATIENT");

        // Mock Patient Service responses
        Mockito.when(patientServiceClient.getPatient(eq(1L), anyString()))
                .thenReturn(Map.of("id", 1L, "firstName", "Peter", "lastName", "Patient"));
        Mockito.when(patientServiceClient.getPatient(eq(999L), anyString()))
                .thenThrow(new PatientNotFoundException("Patient not found with id: 999"));

        // Mock Appointment Service responses
        Mockito.when(appointmentServiceClient.getAppointment(eq(1L), anyString()))
                .thenReturn(Map.of("id", 1L, "status", "CONFIRMED"));

        // Mock Pharmacy Service responses
        Mockito.when(pharmacyServiceClient.getMedicine(eq(2L), anyString()))
                .thenReturn(Map.of("id", 2L, "name", "Paracetamol", "unitPrice", 3.00));
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
    void testBillCreationAndServerSideCalculationWithInsurance() throws Exception {
        // Patient 1 has 80% insurance seeded ($5000 limit)
        // Subtotal: Consultation (1 * $150) + Medicine (10 * $5) = $200.
        // Discount: $20. Total before insurance: $180.
        // Insurance coverage: 80% of ($200 - $20) = $144.
        // Total amount: $180 - $144 = $36. Balance: $36.
        BillItemRequest item1 = new BillItemRequest(ItemType.CONSULTATION, "Doctor Consultation", "DOC-1", 1, new BigDecimal("150.00"));
        BillItemRequest item2 = new BillItemRequest(ItemType.MEDICINE, "Antibiotics", "2", 10, new BigDecimal("5.00"));

        BillRequest req = new BillRequest();
        req.setPatientId(1L);
        req.setAppointmentId(1L);
        req.setItems(List.of(item1, item2));
        req.setDiscountAmount(new BigDecimal("20.00"));
        req.setApplyInsurance(true);
        req.setNotes("Outpatient visit bill");

        MvcResult res = mockMvc.perform(post("/api/bills")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.billNumber").isNotEmpty())
                .andExpect(jsonPath("$.subtotal").value(200.00))
                .andExpect(jsonPath("$.discountAmount").value(20.00))
                .andExpect(jsonPath("$.insuranceAmount").value(144.00))
                .andExpect(jsonPath("$.totalAmount").value(36.00))
                .andExpect(jsonPath("$.balanceAmount").value(36.00))
                .andExpect(jsonPath("$.paidAmount").value(0.00))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        BillResponse created = objectMapper.readValue(res.getResponse().getContentAsString(), BillResponse.class);
        assertNotNull(created.getId());
    }

    @Test
    void testPaymentFlowPartialToFullAndOverpaymentRejection() throws Exception {
        // Create bill for $100 without insurance
        BillItemRequest item = new BillItemRequest(ItemType.TREATMENT, "Physical Therapy", "PT-1", 1, new BigDecimal("100.00"));
        BillRequest req = new BillRequest();
        req.setPatientId(1L);
        req.setItems(List.of(item));
        req.setApplyInsurance(false);

        MvcResult billRes = mockMvc.perform(post("/api/bills")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        BillResponse bill = objectMapper.readValue(billRes.getResponse().getContentAsString(), BillResponse.class);

        // 1. Partial payment of $40
        PaymentRequest partialPay = new PaymentRequest(bill.getId(), new BigDecimal("40.00"), PaymentMethod.CARD, "Part 1 via Card");
        mockMvc.perform(post("/api/payments")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialPay)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        // Verify bill is now PARTIALLY_PAID with balance $60
        mockMvc.perform(get("/api/bills/" + bill.getId())
                        .header("Authorization", receptionistToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PARTIALLY_PAID"))
                .andExpect(jsonPath("$.paidAmount").value(40.00))
                .andExpect(jsonPath("$.balanceAmount").value(60.00));

        // 2. Overpayment rejection: trying to pay $70 when balance is $60
        PaymentRequest overPay = new PaymentRequest(bill.getId(), new BigDecimal("70.00"), PaymentMethod.CASH, "Overpayment attempt");
        mockMvc.perform(post("/api/payments")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overPay)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("exceeds remaining balance")));

        // 3. Final payment of remaining $60 -> Bill becomes PAID
        PaymentRequest finalPay = new PaymentRequest(bill.getId(), new BigDecimal("60.00"), PaymentMethod.UPI, "Final settlement");
        mockMvc.perform(post("/api/payments")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finalPay)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        mockMvc.perform(get("/api/bills/" + bill.getId())
                        .header("Authorization", receptionistToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.paidAmount").value(100.00))
                .andExpect(jsonPath("$.balanceAmount").value(0.00));
    }

    @Test
    void testPaymentRefundAndCancellationRejection() throws Exception {
        // Create bill and full payment
        BillItemRequest item = new BillItemRequest(ItemType.LAB, "Urinalysis", "LAB-1", 1, new BigDecimal("50.00"));
        BillRequest req = new BillRequest();
        req.setPatientId(1L);
        req.setItems(List.of(item));
        req.setApplyInsurance(false);

        MvcResult billRes = mockMvc.perform(post("/api/bills")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        BillResponse bill = objectMapper.readValue(billRes.getResponse().getContentAsString(), BillResponse.class);

        PaymentRequest payReq = new PaymentRequest(bill.getId(), new BigDecimal("50.00"), PaymentMethod.CASH, "Initial Cash");
        MvcResult payRes = mockMvc.perform(post("/api/payments")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payReq)))
                .andExpect(status().isCreated())
                .andReturn();

        PaymentResponse payment = objectMapper.readValue(payRes.getResponse().getContentAsString(), PaymentResponse.class);

        // 1. Refund payment
        mockMvc.perform(post("/api/payments/" + payment.getId() + "/refund")
                        .header("Authorization", receptionistToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        // Verify bill reverted to PENDING with full balance $50
        mockMvc.perform(get("/api/bills/" + bill.getId())
                        .header("Authorization", receptionistToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.paidAmount").value(0.00))
                .andExpect(jsonPath("$.balanceAmount").value(50.00));

        // 2. Cancel bill
        mockMvc.perform(patch("/api/bills/" + bill.getId() + "/cancel")
                        .header("Authorization", receptionistToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        // 3. Payment on cancelled bill is rejected
        mockMvc.perform(post("/api/payments")
                        .header("Authorization", receptionistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("cancelled bill")));
    }

    @Test
    void testPatientBillingAndInsuranceIsolation() throws Exception {
        // Patient 1 can view own bills
        mockMvc.perform(get("/api/bills/patient/1")
                        .header("Authorization", patientToken))
                .andExpect(status().isOk());

        // Patient 1 attempting to view other patient (e.g. patient 99) -> 403 Forbidden
        mockMvc.perform(get("/api/bills/patient/99")
                        .header("Authorization", patientToken))
                .andExpect(status().isForbidden());

        // Other patient (userId 99) attempting to view Patient 1's bills -> 403 Forbidden
        mockMvc.perform(get("/api/bills/patient/1")
                        .header("Authorization", otherPatientToken))
                .andExpect(status().isForbidden());

        // Patient 1 can view own insurance
        mockMvc.perform(get("/api/insurance/patient/1")
                        .header("Authorization", patientToken))
                .andExpect(status().isOk());

        // Other patient attempting to view Patient 1's insurance -> 403 Forbidden
        mockMvc.perform(get("/api/insurance/patient/1")
                        .header("Authorization", otherPatientToken))
                .andExpect(status().isForbidden());
    }
}
