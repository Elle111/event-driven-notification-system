package com.app.notification.api.dto;

import com.app.notification.api.entity.Notification;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private String recipient;
    private String message;
    private Notification.NotificationType type;
    private Notification.NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NotificationResponse() {}

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.recipient = notification.getRecipient();
        this.message = notification.getMessage();
        this.type = notification.getType();
        this.status = notification.getStatus();
        this.createdAt = notification.getCreatedAt();
        this.updatedAt = notification.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Notification.NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(Notification.NotificationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
