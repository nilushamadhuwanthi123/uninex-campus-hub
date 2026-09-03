package com.niluverse.uninex.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.niluverse.uninex.notification.NotificationService;
import com.niluverse.uninex.notification.NotificationType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository repository;

    @Mock
    private QrCodeGenerator qrCodeGenerator;

    @Mock
    private NotificationService notificationService;

    private BookingService service;

    private final Instant future1h = Instant.now().plus(1, ChronoUnit.HOURS);
    private final Instant future2h = Instant.now().plus(2, ChronoUnit.HOURS);
    private final Instant future3h = Instant.now().plus(3, ChronoUnit.HOURS);

    @BeforeEach
    void setUp() {
        service = new BookingService(repository, qrCodeGenerator, notificationService);
    }

    @Test
    void create_savesBooking_whenNoConflict() {
        when(repository.findByResourceIdAndStatusNotInAndStartTimeLessThan(
            anyString(), anyList(), any())).thenReturn(List.of());
        when(repository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingRequest request = new BookingRequest(
            "hall-1", List.of("A1", "A2"), future1h, future2h, "Nilusha", "nilusha@example.com"
        );

        Booking created = service.create(request);

        assertThat(created.getResourceId()).isEqualTo("hall-1");
        assertThat(created.getStatus()).isEqualTo(BookingStatus.REQUESTED);
    }

    @Test
    void create_throwsConflict_whenStartIsNotBeforeEnd() {
        BookingRequest request = new BookingRequest(
            "hall-1", List.of("A1"), future2h, future1h, "Nilusha", "nilusha@example.com"
        );

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(BookingConflictException.class);
    }

    @Test
    void create_throwsConflict_whenSameSeatOverlapsInTime() {
        Booking existing = new Booking("hall-1", List.of("A1"), future1h, future3h, "Other", "o@x.com");
        when(repository.findByResourceIdAndStatusNotInAndStartTimeLessThan(
            anyString(), anyList(), any())).thenReturn(List.of(existing));

        BookingRequest request = new BookingRequest(
            "hall-1", List.of("A1", "A2"), future1h.plusSeconds(60), future2h, "Nilusha", "n@x.com"
        );

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(BookingConflictException.class);
    }

    @Test
    void create_allowsDifferentSeats_forOverlappingTimeOnSameResource() {
        Booking existing = new Booking("hall-1", List.of("A1"), future1h, future3h, "Other", "o@x.com");
        when(repository.findByResourceIdAndStatusNotInAndStartTimeLessThan(
            anyString(), anyList(), any())).thenReturn(List.of(existing));
        when(repository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingRequest request = new BookingRequest(
            "hall-1", List.of("B1"), future1h.plusSeconds(60), future2h, "Nilusha", "n@x.com"
        );

        Booking created = service.create(request);

        assertThat(created.getSeatNumbers()).containsExactly("B1");
    }

    @Test
    void create_throwsConflict_whenFullResourceBookingOverlapsAnyExistingBooking() {
        Booking existing = new Booking("hall-1", List.of("A1"), future1h, future3h, "Other", "o@x.com");
        when(repository.findByResourceIdAndStatusNotInAndStartTimeLessThan(
            anyString(), anyList(), any())).thenReturn(List.of(existing));

        BookingRequest request = new BookingRequest(
            "hall-1", List.of(), future1h.plusSeconds(60), future2h, "Nilusha", "n@x.com"
        );

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(BookingConflictException.class);
    }

    @Test
    void create_allowsBooking_whenTimeWindowsDoNotOverlap() {
        Booking existing = new Booking("hall-1", List.of("A1"), future1h, future2h, "Other", "o@x.com");
        when(repository.findByResourceIdAndStatusNotInAndStartTimeLessThan(
            anyString(), anyList(), any())).thenReturn(List.of(existing));
        when(repository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingRequest request = new BookingRequest(
            "hall-1", List.of("A1"), future2h, future3h, "Nilusha", "n@x.com"
        );

        Booking created = service.create(request);

        assertThat(created).isNotNull();
    }

    @Test
    void approve_setsStatusAndGeneratesTicketAndQrCode() {
        Booking booking = new Booking("hall-1", List.of("A1"), future1h, future2h, "Nilusha", "n@x.com");
        booking.setId("b1");
        when(repository.findById("b1")).thenReturn(java.util.Optional.of(booking));
        when(repository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        when(qrCodeGenerator.generateBase64Png(any())).thenReturn("fake-base64-png");

        Booking approved = service.approve("b1");

        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(approved.getTicketCode()).startsWith("UNX-");
        assertThat(approved.getQrCodeBase64()).isEqualTo("fake-base64-png");
        verify(notificationService).create(
            org.mockito.ArgumentMatchers.eq("n@x.com"),
            org.mockito.ArgumentMatchers.eq(NotificationType.BOOKING_APPROVED),
            org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.any());
    }

    @Test
    void reject_setsStatus_withoutGeneratingTicket() {
        Booking booking = new Booking("hall-1", List.of("A1"), future1h, future2h, "Nilusha", "n@x.com");
        booking.setId("b2");
        when(repository.findById("b2")).thenReturn(java.util.Optional.of(booking));
        when(repository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking rejected = service.reject("b2");

        assertThat(rejected.getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(rejected.getTicketCode()).isNull();
        verify(notificationService).create(
            org.mockito.ArgumentMatchers.eq("n@x.com"),
            org.mockito.ArgumentMatchers.eq(NotificationType.BOOKING_REJECTED),
            org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.any());
    }
}
