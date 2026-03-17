package com.app.notification.api.service;

import com.app.notification.api.dto.NotificationRequest;
import com.app.notification.api.dto.NotificationResponse;
import com.app.notification.api.entity.Notification;
import com.app.notification.api.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = new Notification(
            request.getRecipient(),
            request.getMessage(),
            request.getType()
        );
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // TODO: Publish NotificationCreatedEvent to Kafka (will be implemented in next milestone)
        
        return new NotificationResponse(savedNotification);
    }

    @Transactional(readOnly = true)
    public Optional<NotificationResponse> getNotificationById(Long id) {
        return notificationRepository.findById(id)
            .map(NotificationResponse::new);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipient(recipient)
            .stream()
            .map(NotificationResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByStatus(Notification.NotificationStatus status) {
        return notificationRepository.findByStatus(status)
            .stream()
            .map(NotificationResponse::new)
            .collect(Collectors.toList());
    }

    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll()
            .stream()
            .map(NotificationResponse::new)
            .collect(Collectors.toList());
    }
}
