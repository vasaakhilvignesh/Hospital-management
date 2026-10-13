package com.hospital.notificationservice.controller;

import com.hospital.notificationservice.dto.NotificationRequest;
import com.hospital.notificationservice.dto.NotificationResponse;
import com.hospital.notificationservice.dto.UnreadCountResponse;
import com.hospital.notificationservice.security.UserPrincipal;
import com.hospital.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF')")
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody NotificationRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        return new ResponseEntity<>(notificationService.createNotification(req, principal), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.getNotifications(principal));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<NotificationResponse> getNotificationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.getNotificationById(id, principal));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId, principal));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.markAsRead(id, principal));
    }

    @PatchMapping("/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllAsRead(principal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.getUnreadCount(principal));
    }
}
