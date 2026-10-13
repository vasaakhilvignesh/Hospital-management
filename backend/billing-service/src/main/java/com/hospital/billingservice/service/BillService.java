package com.hospital.billingservice.service;

import com.hospital.billingservice.client.AppointmentServiceClient;
import com.hospital.billingservice.client.PatientServiceClient;
import com.hospital.billingservice.client.PharmacyServiceClient;
import com.hospital.billingservice.dto.BillItemRequest;
import com.hospital.billingservice.dto.BillRequest;
import com.hospital.billingservice.dto.BillResponse;
import com.hospital.billingservice.entity.*;
import com.hospital.billingservice.exception.BillNotFoundException;
import com.hospital.billingservice.exception.BillOperationException;
import com.hospital.billingservice.repository.BillRepository;
import com.hospital.billingservice.repository.InsuranceRepository;
import com.hospital.billingservice.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillService {

    private final BillRepository billRepository;
    private final InsuranceRepository insuranceRepository;
    private final PatientServiceClient patientServiceClient;
    private final AppointmentServiceClient appointmentServiceClient;
    private final PharmacyServiceClient pharmacyServiceClient;

    public BillService(BillRepository billRepository,
                       InsuranceRepository insuranceRepository,
                       PatientServiceClient patientServiceClient,
                       AppointmentServiceClient appointmentServiceClient,
                       PharmacyServiceClient pharmacyServiceClient) {
        this.billRepository = billRepository;
        this.insuranceRepository = insuranceRepository;
        this.patientServiceClient = patientServiceClient;
        this.appointmentServiceClient = appointmentServiceClient;
        this.pharmacyServiceClient = pharmacyServiceClient;
    }

    public BillResponse createBill(BillRequest req, UserPrincipal principal) {
        String token = principal != null ? principal.getRawToken() : null;

        // 1. Validate patient through Patient Service
        if (token != null) {
            patientServiceClient.getPatient(req.getPatientId(), token);
        }

        // 2. Validate appointment through Appointment Service if supplied
        if (req.getAppointmentId() != null && token != null) {
            appointmentServiceClient.getAppointment(req.getAppointmentId(), token);
        }

        // 3. Validate pharmacy references where applicable
        for (BillItemRequest itemReq : req.getItems()) {
            if (itemReq.getItemType() == ItemType.MEDICINE && itemReq.getReferenceId() != null && token != null) {
                try {
                    Long medId = Long.parseLong(itemReq.getReferenceId());
                    pharmacyServiceClient.getMedicine(medId, token);
                } catch (NumberFormatException ignored) {
                    // non-numeric reference ID (e.g. prescription number)
                } catch (Exception e) {
                    // Gracefully allow if network or fallback
                }
            }
        }

        // 4. Calculate Subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        for (BillItemRequest itemReq : req.getItems()) {
            BigDecimal itemTotal = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }

        BigDecimal discount = req.getDiscountAmount() != null ? req.getDiscountAmount() : BigDecimal.ZERO;
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        BigDecimal tax = req.getTaxAmount() != null ? req.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal totalBeforeInsurance = subtotal.subtract(discount).add(tax);

        // 5. Calculate Insurance Coverage
        BigDecimal insuranceAmount = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(req.getApplyInsurance())) {
            List<Insurance> activeInsurances = insuranceRepository.findByPatientIdAndStatus(req.getPatientId(), InsuranceStatus.ACTIVE);
            LocalDate today = LocalDate.now();

            for (Insurance ins : activeInsurances) {
                if (!today.isBefore(ins.getValidFrom()) && !today.isAfter(ins.getValidUntil())) {
                    BigDecimal covered = subtotal.subtract(discount)
                            .multiply(ins.getCoveragePercentage())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                    if (covered.compareTo(ins.getCoverageLimit()) > 0) {
                        covered = ins.getCoverageLimit();
                    }
                    if (covered.compareTo(totalBeforeInsurance) > 0) {
                        covered = totalBeforeInsurance;
                    }

                    insuranceAmount = covered;
                    break;
                }
            }
        }

        BigDecimal totalAmount = totalBeforeInsurance.subtract(insuranceAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        String billNumber = "BILL-" + (System.currentTimeMillis() % 10000000);
        Bill bill = new Bill(
                billNumber,
                req.getPatientId(),
                req.getAppointmentId(),
                LocalDate.now(),
                subtotal,
                tax,
                discount,
                insuranceAmount,
                totalAmount,
                totalAmount, // initial balanceAmount = totalAmount
                BillStatus.PENDING,
                req.getNotes()
        );

        for (BillItemRequest itemReq : req.getItems()) {
            BigDecimal itemTotal = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            BillItem item = new BillItem(
                    bill,
                    itemReq.getItemType(),
                    itemReq.getDescription(),
                    itemReq.getReferenceId(),
                    itemReq.getQuantity(),
                    itemReq.getUnitPrice(),
                    itemTotal
            );
            bill.addItem(item);
        }

        Bill saved = billRepository.save(bill);
        return BillResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public BillResponse getBillById(Long id, UserPrincipal principal) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new BillNotFoundException("Bill not found with id: " + id));

        validatePatientAccess(bill.getPatientId(), principal);
        return BillResponse.fromEntity(bill);
    }

    @Transactional(readOnly = true)
    public BillResponse getBillByNumber(String billNumber, UserPrincipal principal) {
        Bill bill = billRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new BillNotFoundException("Bill not found with number: " + billNumber));

        validatePatientAccess(bill.getPatientId(), principal);
        return BillResponse.fromEntity(bill);
    }

    @Transactional(readOnly = true)
    public List<BillResponse> getBills(Long patientId, BillStatus status, LocalDate date, UserPrincipal principal) {
        if (principal != null && "PATIENT".equalsIgnoreCase(principal.getRole())) {
            if (patientId == null) {
                try {
                    java.util.Map<String, Object> pat = patientServiceClient.getPatientByUserId(principal.getUserId(), principal.getRawToken());
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

        List<Bill> list;
        if (patientId != null && status != null) {
            list = billRepository.findByPatientIdAndStatus(patientId, status);
        } else if (patientId != null) {
            list = billRepository.findByPatientId(patientId);
        } else if (status != null) {
            list = billRepository.findByStatus(status);
        } else if (date != null) {
            list = billRepository.findByBillDate(date);
        } else {
            list = billRepository.findAll();
        }

        return list.stream()
                .map(BillResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BillResponse> getBillsByPatientId(Long patientId, UserPrincipal principal) {
        validatePatientAccess(patientId, principal);
        return billRepository.findByPatientId(patientId).stream()
                .map(BillResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private void validatePatientAccess(Long patientId, UserPrincipal principal) {
        if (principal == null || !"PATIENT".equalsIgnoreCase(principal.getRole())) {
            return;
        }
        try {
            java.util.Map<String, Object> pat = patientServiceClient.getPatient(patientId, principal.getRawToken());
            Object uid = pat.get("userId");
            if (uid != null && uid.toString().equals(principal.getUserId().toString())) {
                return;
            }
        } catch (Exception ignored) {}
        throw new AccessDeniedException("Access denied: You can only view your own bills");
    }

    public BillResponse updateBill(Long id, BillRequest req, UserPrincipal principal) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new BillNotFoundException("Bill not found with id: " + id));

        if (bill.getStatus() != BillStatus.DRAFT && bill.getStatus() != BillStatus.PENDING) {
            throw new BillOperationException("Cannot modify bill in status: " + bill.getStatus());
        }

        // Recalculate subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        bill.getItems().clear();

        for (BillItemRequest itemReq : req.getItems()) {
            BigDecimal itemTotal = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            subtotal = subtotal.add(itemTotal);
            BillItem item = new BillItem(
                    bill,
                    itemReq.getItemType(),
                    itemReq.getDescription(),
                    itemReq.getReferenceId(),
                    itemReq.getQuantity(),
                    itemReq.getUnitPrice(),
                    itemTotal
            );
            bill.addItem(item);
        }

        BigDecimal discount = req.getDiscountAmount() != null ? req.getDiscountAmount() : BigDecimal.ZERO;
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        BigDecimal tax = req.getTaxAmount() != null ? req.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal totalBeforeInsurance = subtotal.subtract(discount).add(tax);

        BigDecimal insuranceAmount = bill.getInsuranceAmount(); // maintain current or reapply
        BigDecimal totalAmount = totalBeforeInsurance.subtract(insuranceAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        bill.setSubtotal(subtotal);
        bill.setDiscountAmount(discount);
        bill.setTaxAmount(tax);
        bill.setTotalAmount(totalAmount);
        bill.setBalanceAmount(totalAmount.subtract(bill.getPaidAmount()));
        bill.setNotes(req.getNotes());

        return BillResponse.fromEntity(billRepository.save(bill));
    }

    public BillResponse cancelBill(Long id, UserPrincipal principal) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new BillNotFoundException("Bill not found with id: " + id));

        if (bill.getStatus() == BillStatus.PAID) {
            throw new BillOperationException("Cannot cancel a fully paid bill");
        }

        bill.setStatus(BillStatus.CANCELLED);
        return BillResponse.fromEntity(billRepository.save(bill));
    }
}
