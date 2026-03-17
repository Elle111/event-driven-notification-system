package com.app.notification.api.repository;

import com.app.notification.api.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findById(Long id);

    List<Notification> findByRecipient(String recipient);

    List<Notification> findByStatus(Notification.NotificationStatus status);

    @Query("SELECT n FROM Notification n WHERE n.recipient = ?1 AND n.status = ?2")
    List<Notification> findByRecipientAndStatus(String recipient, Notification.NotificationStatus status);
}
