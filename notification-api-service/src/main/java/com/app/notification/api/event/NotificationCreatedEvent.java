package com.app.notification.api.event;

import com.app.notification.api.entity.Notification;

import java.time.LocalDateTime;

public class NotificationCreatedEvent {

    private Long notificationId;
    private String recipient;
    private String message;
    private Notification.NotificationType type;
    private LocalDateTime createdAt;

    public NotificationCreatedEvent() {}

    public NotificationCreatedEvent(Notification notification) {
        this.notificationId = notification.getId();
        this.recipient = notification.getRecipient();
        this.message = notification.getMessage();
        this.type = notification.getType();
        this.createdAt = notification.getCreatedAt();
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Notification.NotificationType getType() {
        return type;
    }

    public void setType(Notification.NotificationType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "NotificationCreatedEvent{" +
                "notificationId=" + notificationId +
                ", recipient='" + recipient + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", createdAt=" + createdAt +
                '}';
    }
}
