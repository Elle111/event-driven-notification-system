package com.app.notification.api.dto;

import com.app.notification.api.entity.Notification.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NotificationRequest {

    @NotBlank(message = "Recipient is required")
    @Email(message = "Recipient must be a valid email")
    private String recipient;

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String message;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    public NotificationRequest() {}

    public NotificationRequest(String recipient, String message, NotificationType type) {
        this.recipient = recipient;
        this.message = message;
        this.type = type;
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}
