package com.niluverse.uninex.notification;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> list(@RequestParam String recipientEmail) {
        return ResponseEntity.ok()
            .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache")
            .body(service.findForRecipient(recipientEmail));
    }

    @PostMapping("/{id}/read")
    public Notification markRead(@PathVariable String id) {
        return service.markRead(id);
    }

    /**
     * Real-time push: the browser opens this and keeps it open, and
     * NotificationService.push() writes straight into it as soon as a
     * booking/incident event happens -- no polling.
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String recipientEmail) {
        return service.subscribe(recipientEmail);
    }
}
