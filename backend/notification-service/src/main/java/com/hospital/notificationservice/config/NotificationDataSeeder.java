package com.hospital.notificationservice.config;

import com.hospital.notificationservice.entity.Notification;
import com.hospital.notificationservice.entity.NotificationStatus;
import com.hospital.notificationservice.entity.NotificationType;
import com.hospital.notificationservice.repository.NotificationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationDataSeeder implements CommandLineRunner {

    private final NotificationRepository notificationRepository;

    public NotificationDataSeeder(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void run(String... args) {
        if (notificationRepository.count() > 0) {
            return;
        }

        // 1. Appointment reminder for Patient (userId 5, patientId 1) - UNREAD
        notificationRepository.save(new Notification(
                "NOTIF-APT-001",
                5L,
                1L,
                NotificationType.APPOINTMENT,
                "Upcoming Consultation Reminder",
                "Your consultation with Dr. John Smith is scheduled for tomorrow at 10:00 AM.",
                NotificationStatus.UNREAD
        ));

        // 2. Billing receipt for Patient (userId 5, patientId 1) - READ
        Notification billNotif = new Notification(
                "NOTIF-BIL-002",
                5L,
                1L,
                NotificationType.BILLING,
                "Payment Receipt Confirmed",
                "Payment of $120.00 for BILL-1003 was successfully recorded.",
                NotificationStatus.READ
        );
        billNotif.setReadAt(LocalDateTime.now().minusDays(1));
        notificationRepository.save(billNotif);

        // 3. Prescription notification for Patient (userId 5, patientId 1) - UNREAD
        notificationRepository.save(new Notification(
                "NOTIF-RX-003",
                5L,
                1L,
                NotificationType.PRESCRIPTION,
                "Prescription Ready for Pickup",
                "Your prescription RX-1001 for Amoxicillin has been dispensed by the pharmacy.",
                NotificationStatus.UNREAD
        ));

        // 4. Pharmacy low-stock alert for Admin (userId 1) - UNREAD
        notificationRepository.save(new Notification(
                "NOTIF-PHARM-004",
                1L,
                null,
                NotificationType.PHARMACY,
                "Low Stock Alert: Ibuprofen 400mg",
                "Batch BATCH-IBU-001 has reached threshold level (5 remaining).",
                NotificationStatus.UNREAD
        ));
    }
}
