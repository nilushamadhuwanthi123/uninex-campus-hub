package com.niluverse.uninex.notification;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Creates real notifications (persisted, so there's a real inbox/history)
 * and pushes them live over Server-Sent Events to any browser currently
 * subscribed for that recipient. If nobody is subscribed right now, the
 * notification still exists and shows up next time they call
 * GET /api/notifications -- delivery never depends on someone being
 * online at the exact moment.
 */
@Service
public class NotificationService {

    private static final long EMITTER_TIMEOUT_MS = 30 * 60 * 1000L; // 30 minutes

    private final NotificationRepository repository;
    private final Map<String, List<SseEmitter>> emittersByRecipient = new ConcurrentHashMap<>();

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification create(String recipientEmail, NotificationType type, String message, String relatedResourceId) {
        Notification notification = repository.save(
            new Notification(recipientEmail, type, message, relatedResourceId));
        push(notification);
        return notification;
    }

    public List<Notification> findForRecipient(String recipientEmail) {
        return repository.findByRecipientEmailOrderByCreatedAtDesc(recipientEmail);
    }

    public Notification markRead(String id) {
        Notification notification = repository.findById(id)
            .orElseThrow(() -> new NotificationNotFoundException(id));
        notification.setRead(true);
        return repository.save(notification);
    }

    public SseEmitter subscribe(String recipientEmail) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        List<SseEmitter> emitters = emittersByRecipient.computeIfAbsent(
            recipientEmail, key -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);

        Runnable cleanup = () -> emitters.remove(emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(ex -> cleanup.run());

        return emitter;
    }

    private void push(Notification notification) {
        List<SseEmitter> emitters = emittersByRecipient.get(notification.getRecipientEmail());
        if (emitters == null || emitters.isEmpty()) {
            return;
        }
        for (SseEmitter emitter : List.copyOf(emitters)) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (IOException | IllegalStateException ex) {
                emitters.remove(emitter);
            }
        }
    }
}
