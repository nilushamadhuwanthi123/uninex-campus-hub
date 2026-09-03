package com.niluverse.uninex.incident;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository repository;

    private IncidentService service;

    @BeforeEach
    void setUp() {
        service = new IncidentService(repository);
    }

    private Incident sampleIncident() {
        Incident incident = new Incident(
            "hall-1", "Projector not turning on", "No power light",
            IncidentSeverity.HIGH, "Nilusha", "n@x.com"
        );
        incident.setId("i1");
        return incident;
    }

    @Test
    void create_savesIncidentWithOpenStatus() {
        when(repository.save(any(Incident.class))).thenAnswer(inv -> inv.getArgument(0));
        IncidentRequest request = new IncidentRequest(
            "hall-1", "AC not cooling", "Room too warm", IncidentSeverity.MEDIUM, "Nilusha", "n@x.com"
        );

        Incident created = service.create(request);

        assertThat(created.getStatus()).isEqualTo(IncidentStatus.OPEN);
        assertThat(created.getSeverity()).isEqualTo(IncidentSeverity.MEDIUM);
    }

    @Test
    void assignTechnician_setsNameAndStatus() {
        when(repository.findById("i1")).thenReturn(Optional.of(sampleIncident()));
        when(repository.save(any(Incident.class))).thenAnswer(inv -> inv.getArgument(0));

        Incident assigned = service.assignTechnician("i1", "Kasun");

        assertThat(assigned.getAssignedTechnician()).isEqualTo("Kasun");
        assertThat(assigned.getStatus()).isEqualTo(IncidentStatus.ASSIGNED);
    }

    @Test
    void startWork_throwsIllegalState_whenNoTechnicianAssigned() {
        when(repository.findById("i1")).thenReturn(Optional.of(sampleIncident()));

        assertThatThrownBy(() -> service.startWork("i1"))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void startWork_setsInProgress_whenTechnicianAssigned() {
        Incident incident = sampleIncident();
        incident.setAssignedTechnician("Kasun");
        when(repository.findById("i1")).thenReturn(Optional.of(incident));
        when(repository.save(any(Incident.class))).thenAnswer(inv -> inv.getArgument(0));

        Incident started = service.startWork("i1");

        assertThat(started.getStatus()).isEqualTo(IncidentStatus.IN_PROGRESS);
    }

    @Test
    void resolve_setsResolvedStatusAndTimestamp() {
        when(repository.findById("i1")).thenReturn(Optional.of(sampleIncident()));
        when(repository.save(any(Incident.class))).thenAnswer(inv -> inv.getArgument(0));

        Incident resolved = service.resolve("i1");

        assertThat(resolved.getStatus()).isEqualTo(IncidentStatus.RESOLVED);
        assertThat(resolved.getResolvedAt()).isNotNull();
    }

    @Test
    void findById_throwsNotFound_whenMissing() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("missing"))
            .isInstanceOf(IncidentNotFoundException.class);
    }
}
