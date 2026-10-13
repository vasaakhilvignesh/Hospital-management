package com.hospital.notificationservice.repository;

import com.hospital.notificationservice.entity.Notification;
import com.hospital.notificationservice.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByNotificationCode(String notificationCode);
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus status);
    long countByUserIdAndStatus(Long userId, NotificationStatus status);
}
