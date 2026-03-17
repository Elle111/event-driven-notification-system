package com.app.notification.api;

import com.app.notification.api.dto.NotificationRequest;
import com.app.notification.api.dto.NotificationResponse;
import com.app.notification.api.entity.Notification;
import com.app.notification.api.repository.NotificationRepository;
import com.app.notification.api.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification testNotification;
    private NotificationRequest testRequest;

    @BeforeEach
    void setUp() {
        testNotification = new Notification("test@example.com", "Test message", Notification.NotificationType.EMAIL);
        testNotification.setId(1L);
        testNotification.setStatus(Notification.NotificationStatus.PENDING);
        
        testRequest = new NotificationRequest("test@example.com", "Test message", Notification.NotificationType.EMAIL);
    }

    @Test
    void createNotification_ShouldSaveAndReturnNotificationResponse() {
        // Given
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        NotificationResponse response = notificationService.createNotification(testRequest);

        // Then
        assertNotNull(response);
        assertEquals(testNotification.getId(), response.getId());
        assertEquals(testNotification.getRecipient(), response.getRecipient());
        assertEquals(testNotification.getMessage(), response.getMessage());
        assertEquals(testNotification.getType(), response.getType());
        assertEquals(Notification.NotificationStatus.PENDING, response.getStatus());
        
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getNotificationById_WhenNotificationExists_ShouldReturnNotificationResponse() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        // When
        Optional<NotificationResponse> response = notificationService.getNotificationById(1L);

        // Then
        assertTrue(response.isPresent());
        assertEquals(testNotification.getId(), response.get().getId());
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void getNotificationById_WhenNotificationNotExists_ShouldReturnEmpty() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<NotificationResponse> response = notificationService.getNotificationById(1L);

        // Then
        assertFalse(response.isPresent());
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void getNotificationsByRecipient_ShouldReturnNotificationResponses() {
        // Given
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findByRecipient("test@example.com")).thenReturn(notifications);

        // When
        List<NotificationResponse> responses = notificationService.getNotificationsByRecipient("test@example.com");

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testNotification.getRecipient(), responses.get(0).getRecipient());
        verify(notificationRepository, times(1)).findByRecipient("test@example.com");
    }

    @Test
    void getNotificationsByStatus_ShouldReturnNotificationResponses() {
        // Given
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findByStatus(Notification.NotificationStatus.PENDING)).thenReturn(notifications);

        // When
        List<NotificationResponse> responses = notificationService.getNotificationsByStatus(Notification.NotificationStatus.PENDING);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(Notification.NotificationStatus.PENDING, responses.get(0).getStatus());
        verify(notificationRepository, times(1)).findByStatus(Notification.NotificationStatus.PENDING);
    }
}
