package com.hospital.pharmacyservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.pharmacyservice.client.DoctorServiceClient;
import com.hospital.pharmacyservice.client.PatientServiceClient;
import com.hospital.pharmacyservice.dto.InventoryRequest;
import com.hospital.pharmacyservice.dto.MedicineRequest;
import com.hospital.pharmacyservice.dto.PrescriptionItemRequest;
import com.hospital.pharmacyservice.dto.PrescriptionRequest;
import com.hospital.pharmacyservice.dto.PrescriptionResponse;
import com.hospital.pharmacyservice.entity.Inventory;
import com.hospital.pharmacyservice.entity.Medicine;
import com.hospital.pharmacyservice.entity.MedicineStatus;
import com.hospital.pharmacyservice.repository.InventoryRepository;
import com.hospital.pharmacyservice.repository.MedicineRepository;
import com.hospital.pharmacyservice.repository.PrescriptionRepository;
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
class PharmacyServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @MockBean
    private PatientServiceClient patientServiceClient;

    @MockBean
    private DoctorServiceClient doctorServiceClient;

    private String adminToken;
    private String doctorToken;
    private String pharmacistToken;
    private String patientToken;
    private String otherPatientToken;

    @BeforeEach
    void setUp() {
        adminToken = "Bearer " + generateRealToken(1L, "admin", "ADMIN");
        doctorToken = "Bearer " + generateRealToken(2L, "doctor", "DOCTOR");
        pharmacistToken = "Bearer " + generateRealToken(3L, "pharmacist", "PHARMACIST");
        patientToken = "Bearer " + generateRealToken(5L, "patient", "PATIENT");
        otherPatientToken = "Bearer " + generateRealToken(99L, "otherpatient", "PATIENT");

        // Mock Patient Service responses
        Mockito.when(patientServiceClient.getPatient(eq(5L), anyString()))
                .thenReturn(Map.of("id", 5L, "firstName", "Peter", "lastName", "Patient"));

        // Mock Doctor Service responses
        Mockito.when(doctorServiceClient.getDoctor(eq(2L), anyString()))
                .thenReturn(Map.of("id", 2L, "firstName", "John", "lastName", "Smith"));
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
    void testMedicineManagement() throws Exception {
        MedicineRequest req = new MedicineRequest();
        req.setMedicineCode("MED-TEST-001");
        req.setName("Ciprofloxacin");
        req.setGenericName("Ciprofloxacin HCl");
        req.setCategory("Antibiotics");
        req.setDosageForm("Tablet");
        req.setStrength("500mg");
        req.setUnitPrice(new BigDecimal("18.50"));
        req.setPrescriptionRequired(true);
        req.setStatus(MedicineStatus.ACTIVE);

        // 1. Create medicine as Admin
        mockMvc.perform(post("/api/medicines")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.medicineCode").value("MED-TEST-001"))
                .andExpect(jsonPath("$.name").value("Ciprofloxacin"));

        // 2. Search medicines
        mockMvc.perform(get("/api/medicines")
                        .header("Authorization", doctorToken)
                        .param("search", "Cipro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicineCode").value("MED-TEST-001"));
    }

    @Test
    void testInventoryAlertsAndBatchManagement() throws Exception {
        // 1. Check low stock endpoint: BATCH-IBU-001 should be returned (seeded quantity 5 <= reorder level 20)
        mockMvc.perform(get("/api/inventory/low-stock")
                        .header("Authorization", pharmacistToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.batchNumber == 'BATCH-IBU-001')]").exists());

        // 2. Check expiring endpoint: BATCH-METF-EXP should be returned (expires in 12 days)
        mockMvc.perform(get("/api/inventory/expiring")
                        .header("Authorization", pharmacistToken)
                        .param("daysAhead", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.batchNumber == 'BATCH-METF-EXP')]").exists());
    }

    @Test
    void testPrescriptionAndFifoDispenseFlow() throws Exception {
        Medicine amox = medicineRepository.findByMedicineCode("MED-AMOX-500").orElseThrow();

        // Initial state: BATCH-AMOX-001 has 30, BATCH-AMOX-002 has 70
        Inventory b1Before = inventoryRepository.findByMedicineIdAndBatchNumber(amox.getId(), "BATCH-AMOX-001").orElseThrow();
        Inventory b2Before = inventoryRepository.findByMedicineIdAndBatchNumber(amox.getId(), "BATCH-AMOX-002").orElseThrow();
        int initialB1 = b1Before.getQuantityAvailable();
        int initialB2 = b2Before.getQuantityAvailable();

        // 1. Doctor creates prescription for 40 units of Amoxicillin
        PrescriptionItemRequest item = new PrescriptionItemRequest();
        item.setMedicineId(amox.getId());
        item.setDosage("500mg");
        item.setFrequency("Three times daily");
        item.setDurationDays(10);
        item.setQuantity(40);
        item.setInstructions("Take after food");

        PrescriptionRequest rxReq = new PrescriptionRequest();
        rxReq.setPatientId(5L);
        rxReq.setDoctorId(2L);
        rxReq.setDiagnosis("Bacterial Infection");
        rxReq.setNotes("Complete entire antibiotic course");
        rxReq.setItems(List.of(item));

        MvcResult createRes = mockMvc.perform(post("/api/prescriptions")
                        .header("Authorization", doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rxReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.prescriptionNumber").isNotEmpty())
                .andReturn();

        PrescriptionResponse createdRx = objectMapper.readValue(createRes.getResponse().getContentAsString(), PrescriptionResponse.class);

        // 2. Pharmacist dispenses prescription
        mockMvc.perform(patch("/api/prescriptions/" + createdRx.getId() + "/dispense")
                        .header("Authorization", pharmacistToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISPENSED"))
                .andExpect(jsonPath("$.dispensedBy").value("pharmacist"));

        // 3. Verify FIFO deduction: BATCH-AMOX-001 was older (exhausted completely if initial >= 30, otherwise reduced), BATCH-AMOX-002 reduced by remainder
        Inventory b1After = inventoryRepository.findByMedicineIdAndBatchNumber(amox.getId(), "BATCH-AMOX-001").orElseThrow();
        Inventory b2After = inventoryRepository.findByMedicineIdAndBatchNumber(amox.getId(), "BATCH-AMOX-002").orElseThrow();

        assertEquals(0, b1After.getQuantityAvailable(), "Oldest batch BATCH-AMOX-001 should be completely exhausted by FIFO");
        assertEquals(initialB2 - (40 - initialB1), b2After.getQuantityAvailable(), "Newer batch BATCH-AMOX-002 should cover the remaining quantity");

        // 4. Second dispense attempt fails with 400 Bad Request
        mockMvc.perform(patch("/api/prescriptions/" + createdRx.getId() + "/dispense")
                        .header("Authorization", pharmacistToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInsufficientStockDispenseFailure() throws Exception {
        Medicine ibu = medicineRepository.findByMedicineCode("MED-IBU-400").orElseThrow();

        // Ibuprofen only has 5 in stock (BATCH-IBU-001). Create prescription for 20 units.
        PrescriptionItemRequest item = new PrescriptionItemRequest();
        item.setMedicineId(ibu.getId());
        item.setDosage("400mg");
        item.setFrequency("As needed for pain");
        item.setDurationDays(5);
        item.setQuantity(20);

        PrescriptionRequest rxReq = new PrescriptionRequest();
        rxReq.setPatientId(5L);
        rxReq.setDoctorId(2L);
        rxReq.setDiagnosis("Severe sprain");
        rxReq.setItems(List.of(item));

        MvcResult createRes = mockMvc.perform(post("/api/prescriptions")
                        .header("Authorization", doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rxReq)))
                .andExpect(status().isCreated())
                .andReturn();

        PrescriptionResponse createdRx = objectMapper.readValue(createRes.getResponse().getContentAsString(), PrescriptionResponse.class);

        // Dispensing must fail due to InsufficientStockException
        mockMvc.perform(patch("/api/prescriptions/" + createdRx.getId() + "/dispense")
                        .header("Authorization", pharmacistToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Insufficient stock")));
    }

    @Test
    void testPatientPrescriptionIsolation() throws Exception {
        Medicine para = medicineRepository.findByMedicineCode("MED-PARA-650").orElseThrow();

        PrescriptionItemRequest item = new PrescriptionItemRequest();
        item.setMedicineId(para.getId());
        item.setDosage("650mg");
        item.setFrequency("Every 6 hours");
        item.setDurationDays(3);
        item.setQuantity(12);

        PrescriptionRequest rxReq = new PrescriptionRequest();
        rxReq.setPatientId(5L); // Patient 5
        rxReq.setDoctorId(2L);
        rxReq.setDiagnosis("Fever");
        rxReq.setItems(List.of(item));

        MvcResult createRes = mockMvc.perform(post("/api/prescriptions")
                        .header("Authorization", doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rxReq)))
                .andExpect(status().isCreated())
                .andReturn();

        PrescriptionResponse createdRx = objectMapper.readValue(createRes.getResponse().getContentAsString(), PrescriptionResponse.class);

        // Patient 5 can view own prescription
        mockMvc.perform(get("/api/prescriptions/" + createdRx.getId())
                        .header("Authorization", patientToken))
                .andExpect(status().isOk());

        // Other Patient (userId 99) attempting to view Patient 5's prescription -> 403 Forbidden
        mockMvc.perform(get("/api/prescriptions/" + createdRx.getId())
                        .header("Authorization", otherPatientToken))
                .andExpect(status().isForbidden());
    }
}
