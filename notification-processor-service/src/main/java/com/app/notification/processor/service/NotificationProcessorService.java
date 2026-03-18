package com.app.notification.processor.service;

import com.app.notification.processor.entity.Notification;
import com.app.notification.processor.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
public class NotificationProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationProcessorService.class);
    private static final Random random = new Random();

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationProcessorService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void processNotification(Long notificationId) {
        logger.info("Processing notification {}", notificationId);

        // Find notification
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        // Update status to PROCESSING
        notification.setStatus(Notification.NotificationStatus.PROCESSING);
        notificationRepository.save(notification);
        logger.info("Updated notification {} status to PROCESSING", notificationId);

        try {
            // Simulate notification processing (email/SMS/PUSH sending)
            boolean success = sendNotification(notification);
            
            if (success) {
                notification.setStatus(Notification.NotificationStatus.SENT);
                logger.info("Successfully processed notification {}", notificationId);
            } else {
                notification.setStatus(Notification.NotificationStatus.FAILED);
                logger.warn("Failed to process notification {}", notificationId);
            }
            
            notificationRepository.save(notification);
            
        } catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notificationRepository.save(notification);
            logger.error("Error processing notification {}", notificationId, e);
            throw e;
        }
    }

    private boolean sendNotification(Notification notification) {
        // Simulate notification sending with 80% success rate
        try {
            Thread.sleep(1000 + random.nextInt(2000)); // Simulate processing time
            
            // Simulate different success rates for different notification types
            double successRate = switch (notification.getType()) {
                case EMAIL -> 0.9;  // 90% success rate
                case SMS -> 0.8;    // 80% success rate
                case PUSH -> 0.7;   // 70% success rate
            };
            
            boolean success = random.nextDouble() < successRate;
            
            if (success) {
                logger.info("Successfully sent {} notification to {}", 
                    notification.getType(), notification.getRecipient());
            } else {
                logger.warn("Failed to send {} notification to {}", 
                    notification.getType(), notification.getRecipient());
            }
            
            return success;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Notification processing interrupted for {}", notification.getId());
            return false;
        }
    }
}
