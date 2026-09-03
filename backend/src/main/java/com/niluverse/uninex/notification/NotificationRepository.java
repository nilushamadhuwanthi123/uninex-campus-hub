package com.niluverse.uninex.notification;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail);
}
