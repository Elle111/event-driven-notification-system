package com.app.notification.api;

import com.app.notification.api.dto.NotificationRequest;
import com.app.notification.api.entity.Notification;
import com.app.notification.api.repository.NotificationRepository;
import com.app.notification.api.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.app.notification.api.entity.Notification.NotificationType.EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class KafkaIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void createNotification_ShouldPublishToKafka() {
        // Given
        NotificationRequest request = new NotificationRequest("test@example.com", "Test message", EMAIL);

        // When
        var response = notificationService.createNotification(request);

        // Then
        assertNotNull(response);
        assertEquals("test@example.com", response.getRecipient());
        assertEquals("Test message", response.getMessage());
        assertEquals(EMAIL, response.getType());
        assertEquals(Notification.NotificationStatus.PENDING, response.getStatus());

        // Verify notification is saved to database
        Optional<Notification> savedNotification = notificationRepository.findById(response.getId());
        assertTrue(savedNotification.isPresent());
        assertEquals("test@example.com", savedNotification.get().getRecipient());
    }
}
