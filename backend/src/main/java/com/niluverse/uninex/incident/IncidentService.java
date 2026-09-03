package com.niluverse.uninex.incident;

import com.niluverse.uninex.notification.NotificationService;
import com.niluverse.uninex.notification.NotificationType;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class IncidentService {

    private final IncidentRepository repository;
    private final NotificationService notificationService;

    public IncidentService(IncidentRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public List<Incident> findAll() {
        return repository.findAll();
    }

    public List<Incident> findByResource(String resourceId) {
        return repository.findByResourceId(resourceId);
    }

    public List<Incident> findByStatus(IncidentStatus status) {
        return repository.findByStatus(status);
    }

    public Incident findById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new IncidentNotFoundException(id));
    }

    public Incident create(IncidentRequest request) {
        Incident incident = new Incident(
            request.resourceId(), request.title(), request.description(),
            request.severity(), request.reporterName(), request.reporterEmail()
        );
        return repository.save(incident);
    }

    public Incident assignTechnician(String id, String technicianName) {
        Incident incident = findById(id);
        incident.setAssignedTechnician(technicianName);
        incident.setStatus(IncidentStatus.ASSIGNED);
        Incident saved = repository.save(incident);
        notificationService.create(saved.getReporterEmail(), NotificationType.INCIDENT_ASSIGNED,
            technicianName + " was assigned to your report: " + saved.getTitle(), saved.getId());
        return saved;
    }

    public Incident startWork(String id) {
        Incident incident = findById(id);
        if (incident.getAssignedTechnician() == null) {
            throw new IllegalStateException("Cannot start work before a technician is assigned");
        }
        incident.setStatus(IncidentStatus.IN_PROGRESS);
        return repository.save(incident);
    }

    public Incident resolve(String id) {
        Incident incident = findById(id);
        incident.setStatus(IncidentStatus.RESOLVED);
        incident.setResolvedAt(Instant.now());
        Incident saved = repository.save(incident);
        notificationService.create(saved.getReporterEmail(), NotificationType.INCIDENT_RESOLVED,
            "Your report is resolved: " + saved.getTitle(), saved.getId());
        return saved;
    }

    public Incident close(String id) {
        Incident incident = findById(id);
        incident.setStatus(IncidentStatus.CLOSED);
        return repository.save(incident);
    }
}
