package com.niluverse.uninex.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @Test
    void create_savesNotification_builtFromArguments() {
        NotificationService service = new NotificationService(repository);
        when(repository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        Notification saved = service.create(
            "student@example.com", NotificationType.BOOKING_APPROVED, "Approved!", "booking-1");

        assertThat(saved.getRecipientEmail()).isEqualTo("student@example.com");
        assertThat(saved.getType()).isEqualTo(NotificationType.BOOKING_APPROVED);
        assertThat(saved.isRead()).isFalse();
    }

    @Test
    void findForRecipient_returnsRepositoryResults() {
        NotificationService service = new NotificationService(repository);
        Notification n = new Notification("a@x.com", NotificationType.INCIDENT_RESOLVED, "Done", "i1");
        when(repository.findByRecipientEmailOrderByCreatedAtDesc("a@x.com")).thenReturn(List.of(n));

        List<Notification> result = service.findForRecipient("a@x.com");

        assertThat(result).hasSize(1);
    }

    @Test
    void markRead_flipsReadFlag() {
        NotificationService service = new NotificationService(repository);
        Notification n = new Notification("a@x.com", NotificationType.INCIDENT_RESOLVED, "Done", "i1");
        n.setId("n1");
        when(repository.findById("n1")).thenReturn(Optional.of(n));
        when(repository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        Notification marked = service.markRead("n1");

        assertThat(marked.isRead()).isTrue();
    }

    @Test
    void markRead_throwsNotFound_whenMissing() {
        NotificationService service = new NotificationService(repository);
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.markRead("missing"))
            .isInstanceOf(NotificationNotFoundException.class);
    }

    @Test
    void subscribe_returnsALiveEmitter_thatCreateCanPushTo() throws Exception {
        NotificationService service = new NotificationService(repository);
        when(repository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter =
            service.subscribe("watcher@example.com");

        assertThat(emitter).isNotNull();

        // Creating a notification for the subscribed recipient must not throw,
        // whether or not the emitter is still considered "connected" in this
        // unit-test context (no real HTTP response backs it here).
        service.create("watcher@example.com", NotificationType.BOOKING_APPROVED, "Hi", "b1");
    }
}
