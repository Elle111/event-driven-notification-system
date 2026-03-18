package com.app.notification.processor.listener;

import com.app.notification.api.event.NotificationCreatedEvent;
import com.app.notification.processor.service.NotificationProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationProcessorService notificationProcessorService;

    @Autowired
    public NotificationEventListener(NotificationProcessorService notificationProcessorService) {
        this.notificationProcessorService = notificationProcessorService;
    }

    @KafkaListener(
        topics = "notification-created",
        groupId = "notification-processor-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNotificationCreatedEvent(
            @Payload NotificationCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.info("Received NotificationCreatedEvent: {} from topic: {}, partition: {}, offset: {}", 
            event, topic, partition, offset);

        try {
            notificationProcessorService.processNotification(event.getNotificationId());
            logger.info("Successfully processed notification {}", event.getNotificationId());
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Failed to process notification {}: {}", 
                event.getNotificationId(), e.getMessage(), e);
            // Don't acknowledge - let the framework handle retries and dead-letter
            throw e;
        }
    }

    @KafkaListener(
        topics = "notification-created-dead-letter",
        groupId = "notification-processor-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleDeadLetterEvent(
            @Payload NotificationCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.error("Processing dead letter event for notification {} from topic: {}, partition: {}, offset: {}", 
            event.getNotificationId(), topic, partition, offset);
        
        // Here you could implement additional logic for handling failed notifications
        // For example: send alerts, store in separate error table, trigger manual review, etc.
    }
}
