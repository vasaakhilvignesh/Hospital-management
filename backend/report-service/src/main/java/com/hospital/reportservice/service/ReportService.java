package com.hospital.reportservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.reportservice.client.*;
import com.hospital.reportservice.dto.DashboardSummaryResponse;
import com.hospital.reportservice.dto.ReportRequest;
import com.hospital.reportservice.dto.ReportResponse;
import com.hospital.reportservice.entity.Report;
import com.hospital.reportservice.entity.ReportStatus;
import com.hospital.reportservice.entity.ReportType;
import com.hospital.reportservice.repository.ReportRepository;
import com.hospital.reportservice.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;
    private final AppointmentServiceClient appointmentServiceClient;
    private final BillingServiceClient billingServiceClient;
    private final PharmacyServiceClient pharmacyServiceClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReportService(ReportRepository reportRepository,
                         PatientServiceClient patientServiceClient,
                         DoctorServiceClient doctorServiceClient,
                         AppointmentServiceClient appointmentServiceClient,
                         BillingServiceClient billingServiceClient,
                         PharmacyServiceClient pharmacyServiceClient) {
        this.reportRepository = reportRepository;
        this.patientServiceClient = patientServiceClient;
        this.doctorServiceClient = doctorServiceClient;
        this.appointmentServiceClient = appointmentServiceClient;
        this.billingServiceClient = billingServiceClient;
        this.pharmacyServiceClient = pharmacyServiceClient;
    }

    public ReportResponse createReport(ReportRequest req, UserPrincipal principal) {
        String token = principal != null ? principal.getRawToken() : null;

        Long patientId = req.getPatientId();
        if (principal != null && "PATIENT".equalsIgnoreCase(principal.getRole())) {
            // Patient can only generate reports for themselves
            if (patientId != null) {
                validatePatientAccess(patientId, principal);
            }
        }

        String dataSummary = req.getData();
        String description = req.getDescription();

        // Dynamically enhance summary with real REST communication if patientId provided
        if (patientId != null && token != null) {
            Map<String, Object> patient = patientServiceClient.getPatient(patientId, token);

            if (req.getReportType() == ReportType.MEDICAL_SUMMARY) {
                List<Map<String, Object>> histories = patientServiceClient.getMedicalHistories(patientId, token);
                Map<String, Object> summary = new HashMap<>();
                summary.put("patient", patient);
                summary.put("medicalHistories", histories);
                try {
                    dataSummary = objectMapper.writeValueAsString(summary);
                } catch (Exception ignored) {}
                if (description == null || description.isBlank()) {
                    description = "Medical summary report for " + patient.get("firstName") + " " + patient.get("lastName");
                }
            } else if (req.getReportType() == ReportType.APPOINTMENT_SUMMARY) {
                List<Map<String, Object>> apts = appointmentServiceClient.getAppointmentsByPatient(patientId, token);
                long upcoming = apts.stream().filter(a -> "SCHEDULED".equals(a.get("status")) || "CONFIRMED".equals(a.get("status"))).count();
                long completed = apts.stream().filter(a -> "COMPLETED".equals(a.get("status"))).count();
                long cancelled = apts.stream().filter(a -> "CANCELLED".equals(a.get("status"))).count();

                Map<String, Object> summary = new HashMap<>();
                summary.put("totalAppointments", apts.size());
                summary.put("upcoming", upcoming);
                summary.put("completed", completed);
                summary.put("cancelled", cancelled);
                try {
                    dataSummary = objectMapper.writeValueAsString(summary);
                } catch (Exception ignored) {}
                if (description == null || description.isBlank()) {
                    description = "Appointment summary: Total " + apts.size() + ", Upcoming: " + upcoming + ", Completed: " + completed;
                }
            } else if (req.getReportType() == ReportType.BILLING_SUMMARY) {
                List<Map<String, Object>> bills = billingServiceClient.getBillsByPatient(patientId, token);
                BigDecimal totalBilled = BigDecimal.ZERO;
                BigDecimal totalPaid = BigDecimal.ZERO;
                BigDecimal outstanding = BigDecimal.ZERO;

                for (Map<String, Object> b : bills) {
                    if (b.get("totalAmount") != null) totalBilled = totalBilled.add(new BigDecimal(b.get("totalAmount").toString()));
                    if (b.get("paidAmount") != null) totalPaid = totalPaid.add(new BigDecimal(b.get("paidAmount").toString()));
                    if (b.get("balanceAmount") != null) outstanding = outstanding.add(new BigDecimal(b.get("balanceAmount").toString()));
                }

                Map<String, Object> summary = new HashMap<>();
                summary.put("totalBills", bills.size());
                summary.put("totalBilled", totalBilled);
                summary.put("totalPaid", totalPaid);
                summary.put("outstandingAmount", outstanding);
                try {
                    dataSummary = objectMapper.writeValueAsString(summary);
                } catch (Exception ignored) {}
                if (description == null || description.isBlank()) {
                    description = "Billing summary: Total Billed: $" + totalBilled + ", Paid: $" + totalPaid + ", Outstanding: $" + outstanding;
                }
            } else if (req.getReportType() == ReportType.PRESCRIPTION_SUMMARY) {
                List<Map<String, Object>> rxs = pharmacyServiceClient.getPrescriptionsByPatient(patientId, token);
                long pending = rxs.stream().filter(r -> "PENDING".equals(r.get("status"))).count();
                long dispensed = rxs.stream().filter(r -> "DISPENSED".equals(r.get("status"))).count();

                Map<String, Object> summary = new HashMap<>();
                summary.put("totalPrescriptions", rxs.size());
                summary.put("pending", pending);
                summary.put("dispensed", dispensed);
                try {
                    dataSummary = objectMapper.writeValueAsString(summary);
                } catch (Exception ignored) {}
                if (description == null || description.isBlank()) {
                    description = "Prescription summary: Total " + rxs.size() + ", Pending: " + pending + ", Dispensed: " + dispensed;
                }
            }
        }

        String reportCode = "REP-" + (System.currentTimeMillis() % 10000000);
        Report report = new Report(
                reportCode,
                patientId,
                req.getReportType(),
                req.getTitle(),
                description,
                LocalDate.now(),
                ReportStatus.AVAILABLE,
                dataSummary
        );

        return ReportResponse.fromEntity(reportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public ReportResponse getReportById(Long id, UserPrincipal principal) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));

        if (report.getPatientId() != null) {
            validatePatientAccess(report.getPatientId(), principal);
        }

        return ReportResponse.fromEntity(report);
    }

    @Transactional(readOnly = true)
    public ReportResponse getReportByCode(String code, UserPrincipal principal) {
        Report report = reportRepository.findByReportCode(code)
                .orElseThrow(() -> new RuntimeException("Report not found with code: " + code));

        if (report.getPatientId() != null) {
            validatePatientAccess(report.getPatientId(), principal);
        }

        return ReportResponse.fromEntity(report);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReports(Long patientId, ReportType type, UserPrincipal principal) {
        if (principal != null && "PATIENT".equalsIgnoreCase(principal.getRole())) {
            if (patientId == null) {
                try {
                    Map<String, Object> pat = patientServiceClient.getPatientByUserId(principal.getUserId(), principal.getRawToken());
                    if (pat != null && pat.get("id") != null) {
                        patientId = Long.valueOf(pat.get("id").toString());
                    }
                } catch (Exception e) {
                    return List.of();
                }
            } else {
                validatePatientAccess(patientId, principal);
            }
        }

        List<Report> list;
        if (patientId != null) {
            list = reportRepository.findByPatientId(patientId);
        } else if (type != null) {
            list = reportRepository.findByReportType(type);
        } else {
            list = reportRepository.findAll();
        }

        return list.stream()
                .map(ReportResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByPatientId(Long patientId, UserPrincipal principal) {
        validatePatientAccess(patientId, principal);
        return reportRepository.findByPatientId(patientId).stream()
                .map(ReportResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ReportResponse archiveReport(Long id, UserPrincipal principal) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));

        if (report.getPatientId() != null) {
            validatePatientAccess(report.getPatientId(), principal);
        }

        report.setStatus(ReportStatus.ARCHIVED);
        return ReportResponse.fromEntity(reportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary(UserPrincipal principal) {
        String token = principal != null ? principal.getRawToken() : "";

        // 1. Total Patients
        List<Map<String, Object>> patients = patientServiceClient.getAllPatients(token);
        long totalPatients = patients.size();

        // 2. Total Doctors
        List<Map<String, Object>> doctors = doctorServiceClient.getAllDoctors(token);
        long totalDoctors = doctors.size();

        // 3. Appointments metrics
        List<Map<String, Object>> appointments = appointmentServiceClient.getAllAppointments(token);
        long totalAppointments = appointments.size();
        long pendingAppointments = appointments.stream()
                .filter(a -> "SCHEDULED".equals(a.get("status")) || "CONFIRMED".equals(a.get("status")))
                .count();
        long completedAppointments = appointments.stream()
                .filter(a -> "COMPLETED".equals(a.get("status")))
                .count();

        // 4. Billing metrics
        List<Map<String, Object>> bills = billingServiceClient.getAllBills(token);
        BigDecimal outstanding = BigDecimal.ZERO;
        for (Map<String, Object> b : bills) {
            if (b.get("balanceAmount") != null) {
                outstanding = outstanding.add(new BigDecimal(b.get("balanceAmount").toString()));
            }
        }

        // 5. Prescriptions metrics
        List<Map<String, Object>> rxs = pharmacyServiceClient.getAllPrescriptions(token);
        long pendingPrescriptions = rxs.stream()
                .filter(r -> "PENDING".equals(r.get("status")))
                .count();

        // 6. Low stock count
        List<Map<String, Object>> lowStock = pharmacyServiceClient.getLowStock(token);
        long lowStockCount = lowStock.size();

        return new DashboardSummaryResponse(
                totalPatients,
                totalDoctors,
                totalAppointments,
                pendingAppointments,
                completedAppointments,
                outstanding,
                pendingPrescriptions,
                lowStockCount
        );
    }

    private void validatePatientAccess(Long patientId, UserPrincipal principal) {
        if (principal == null || !"PATIENT".equalsIgnoreCase(principal.getRole())) {
            return;
        }
        try {
            Map<String, Object> pat = patientServiceClient.getPatient(patientId, principal.getRawToken());
            Object uid = pat.get("userId");
            if (uid != null && uid.toString().equals(principal.getUserId().toString())) {
                return;
            }
        } catch (Exception ignored) {}
        throw new AccessDeniedException("Access denied: You can only view your own reports");
    }
}
