package com.hospital.notificationservice.dto;

import com.hospital.notificationservice.entity.Notification;
import com.hospital.notificationservice.entity.NotificationStatus;
import com.hospital.notificationservice.entity.NotificationType;
import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private String notificationCode;
    private Long userId;
    private Long patientId;
    private NotificationType type;
    private String title;
    private String message;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public NotificationResponse() {}

    public static NotificationResponse fromEntity(Notification n) {
        NotificationResponse res = new NotificationResponse();
        res.setId(n.getId());
        res.setNotificationCode(n.getNotificationCode());
        res.setUserId(n.getUserId());
        res.setPatientId(n.getPatientId());
        res.setType(n.getType());
        res.setTitle(n.getTitle());
        res.setMessage(n.getMessage());
        res.setStatus(n.getStatus());
        res.setCreatedAt(n.getCreatedAt());
        res.setReadAt(n.getReadAt());
        return res;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNotificationCode() { return notificationCode; }
    public void setNotificationCode(String notificationCode) { this.notificationCode = notificationCode; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}
