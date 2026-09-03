package com.niluverse.uninex.notification;

public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(String id) {
        super("Notification not found: " + id);
    }
}
