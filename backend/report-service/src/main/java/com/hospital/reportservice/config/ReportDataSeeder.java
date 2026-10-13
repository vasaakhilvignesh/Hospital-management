package com.hospital.reportservice.config;

import com.hospital.reportservice.entity.Report;
import com.hospital.reportservice.entity.ReportStatus;
import com.hospital.reportservice.entity.ReportType;
import com.hospital.reportservice.repository.ReportRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReportDataSeeder implements CommandLineRunner {

    private final ReportRepository reportRepository;

    public ReportDataSeeder(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public void run(String... args) {
        if (reportRepository.count() > 0) {
            return;
        }

        LocalDate today = LocalDate.now();

        // 1. Medical Summary Report
        reportRepository.save(new Report(
                "REP-MED-101",
                1L,
                ReportType.MEDICAL_SUMMARY,
                "Patient Annual Health Review",
                "Comprehensive diagnostic and immunization review for Peter Patient",
                today.minusDays(5),
                ReportStatus.AVAILABLE,
                "{\"bloodGroup\":\"O+\",\"allergies\":\"Penicillin\",\"immunizations\":[\"COVID-19\",\"Hepatitis B\"]}"
        ));

        // 2. Appointment Summary Report
        reportRepository.save(new Report(
                "REP-APT-102",
                1L,
                ReportType.APPOINTMENT_SUMMARY,
                "Quarterly Appointment Activity",
                "Appointment log showing 2 scheduled and 1 completed consultation",
                today.minusDays(3),
                ReportStatus.AVAILABLE,
                "{\"total\":3,\"completed\":1,\"upcoming\":2}"
        ));

        // 3. Billing Summary Report
        reportRepository.save(new Report(
                "REP-BIL-103",
                1L,
                ReportType.BILLING_SUMMARY,
                "Patient Account Statement",
                "Billing overview: Total billed $470.00, Insurance covered $200.00, Paid $270.00",
                today.minusDays(2),
                ReportStatus.AVAILABLE,
                "{\"totalBilled\":470.00,\"insuranceCovered\":200.00,\"totalPaid\":270.00,\"outstanding\":0.00}"
        ));

        // 4. Prescription Summary Report
        reportRepository.save(new Report(
                "REP-RX-104",
                1L,
                ReportType.PRESCRIPTION_SUMMARY,
                "Pharmacy Dispense Audit",
                "Prescription log: 1 pending, 2 dispensed medications",
                today.minusDays(1),
                ReportStatus.AVAILABLE,
                "{\"pendingPrescriptions\":1,\"dispensedPrescriptions\":2}"
        ));

        // 5. General Hospital Report
        reportRepository.save(new Report(
                "REP-GEN-105",
                null,
                ReportType.GENERAL,
                "Monthly Operational Efficiency Report",
                "Facility equipment utilization and clinical department readiness index",
                today,
                ReportStatus.AVAILABLE,
                "{\"operationalEfficiency\":\"94%\",\"bedOccupancy\":\"78%\"}"
        ));
    }
}
