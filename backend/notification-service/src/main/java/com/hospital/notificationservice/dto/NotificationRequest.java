package com.hospital.notificationservice.dto;

import com.hospital.notificationservice.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long patientId;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    public NotificationRequest() {}

    public NotificationRequest(Long userId, Long patientId, NotificationType type, String title, String message) {
        this.userId = userId;
        this.patientId = patientId;
        this.type = type;
        this.title = title;
        this.message = message;
    }

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
}
