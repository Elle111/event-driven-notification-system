package com.app.notification.api.service;

import com.app.notification.api.dto.NotificationRequest;
import com.app.notification.api.dto.NotificationResponse;
import com.app.notification.api.entity.Notification;
import com.app.notification.api.event.NotificationCreatedEvent;
import com.app.notification.api.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final String NOTIFICATION_CREATED_TOPIC = "notification-created";

    private final NotificationRepository notificationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, 
                              KafkaTemplate<String, Object> kafkaTemplate) {
        this.notificationRepository = notificationRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = new Notification(
            request.getRecipient(),
            request.getMessage(),
            request.getType()
        );
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Publish NotificationCreatedEvent to Kafka
        NotificationCreatedEvent event = new NotificationCreatedEvent(savedNotification);
        publishNotificationCreatedEvent(event);
        
        logger.info("Created notification {} and published event", savedNotification.getId());
        
        return new NotificationResponse(savedNotification);
    }

    private void publishNotificationCreatedEvent(NotificationCreatedEvent event) {
        try {
            kafkaTemplate.send(NOTIFICATION_CREATED_TOPIC, event.getNotificationId().toString(), event)
                .whenComplete((result, failure) -> {
                    if (failure == null) {
                        logger.info("Successfully published event for notification {}", event.getNotificationId());
                    } else {
                        logger.error("Failed to publish event for notification {}", event.getNotificationId(), failure);
                    }
                });
        } catch (Exception e) {
            logger.error("Error publishing event for notification {}", event.getNotificationId(), e);
            // Continue processing even if Kafka publishing fails
        }
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
