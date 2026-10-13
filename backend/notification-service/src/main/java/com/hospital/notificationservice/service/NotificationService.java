package com.hospital.notificationservice.service;

import com.hospital.notificationservice.dto.NotificationRequest;
import com.hospital.notificationservice.dto.NotificationResponse;
import com.hospital.notificationservice.dto.UnreadCountResponse;
import com.hospital.notificationservice.entity.Notification;
import com.hospital.notificationservice.entity.NotificationStatus;
import com.hospital.notificationservice.repository.NotificationRepository;
import com.hospital.notificationservice.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationResponse createNotification(NotificationRequest req, UserPrincipal principal) {
        String code = "NOTIF-" + (System.currentTimeMillis() % 10000000);
        Notification notif = new Notification(
                code,
                req.getUserId(),
                req.getPatientId(),
                req.getType(),
                req.getTitle(),
                req.getMessage(),
                NotificationStatus.UNREAD
        );
        return NotificationResponse.fromEntity(notificationRepository.save(notif));
    }

    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id, UserPrincipal principal) {
        Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        validateUserAccess(notif.getUserId(), principal);
        return NotificationResponse.fromEntity(notif);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(UserPrincipal principal) {
        if (principal != null && ("ADMIN".equalsIgnoreCase(principal.getRole()) || "RECEPTIONIST".equalsIgnoreCase(principal.getRole()))) {
            return notificationRepository.findAll().stream()
                    .map(NotificationResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        Long userId = principal != null ? principal.getUserId() : -1L;
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUserId(Long userId, UserPrincipal principal) {
        validateUserAccess(userId, principal);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public NotificationResponse markAsRead(Long id, UserPrincipal principal) {
        Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        validateUserAccess(notif.getUserId(), principal);

        notif.setStatus(NotificationStatus.READ);
        notif.setReadAt(LocalDateTime.now());
        return NotificationResponse.fromEntity(notificationRepository.save(notif));
    }

    public void markAllAsRead(UserPrincipal principal) {
        Long userId = principal != null ? principal.getUserId() : -1L;
        List<Notification> unreadList = notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD);
        LocalDateTime now = LocalDateTime.now();
        for (Notification n : unreadList) {
            n.setStatus(NotificationStatus.READ);
            n.setReadAt(now);
        }
        notificationRepository.saveAll(unreadList);
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(UserPrincipal principal) {
        Long userId = principal != null ? principal.getUserId() : -1L;
        long count = notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
        return new UnreadCountResponse(count);
    }

    private void validateUserAccess(Long targetUserId, UserPrincipal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Access denied: Unauthenticated");
        }
        if ("ADMIN".equalsIgnoreCase(principal.getRole()) || "RECEPTIONIST".equalsIgnoreCase(principal.getRole())) {
            return;
        }
        if (!principal.getUserId().equals(targetUserId)) {
            throw new AccessDeniedException("Access denied: You can only view your own notifications");
        }
    }
}
