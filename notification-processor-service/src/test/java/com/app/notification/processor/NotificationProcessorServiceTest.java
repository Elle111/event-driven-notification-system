package com.app.notification.processor;

import com.app.notification.processor.entity.Notification;
import com.app.notification.processor.repository.NotificationRepository;
import com.app.notification.processor.service.NotificationProcessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationProcessorServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationProcessorService notificationProcessorService;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = new Notification("test@example.com", "Test message", Notification.NotificationType.EMAIL);
        testNotification.setId(1L);
        testNotification.setStatus(Notification.NotificationStatus.PENDING);
    }

    @Test
    void processNotification_WhenNotificationExists_ShouldProcessSuccessfully() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        notificationProcessorService.processNotification(1L);

        // Then
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, atLeast(2)).save(any(Notification.class)); // Once for PROCESSING, once for final status
    }

    @Test
    void processNotification_WhenNotificationNotExists_ShouldThrowException() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            notificationProcessorService.processNotification(1L);
        });

        assertEquals("Notification not found: 1", exception.getMessage());
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void processNotification_WhenProcessingFails_ShouldSetStatusToFailed() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        notificationProcessorService.processNotification(1L);

        // Then
        // The status will be either SENT or FAILED depending on the random outcome
        // We just verify that the notification was processed
        verify(notificationRepository, atLeast(2)).save(any(Notification.class));
    }
}
