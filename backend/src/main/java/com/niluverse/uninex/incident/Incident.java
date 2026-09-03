package com.niluverse.uninex.incident;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A maintenance/fault report against a Resource -- e.g. "Projector in
 * Lab 3 not turning on". assignedTechnician is a plain name for now
 * (real technician accounts land with Issue #5's auth work); the
 * lifecycle is OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED.
 */
@Document(collection = "incidents")
public class Incident {

    @Id
    private String id;

    private String resourceId;
    private String title;
    private String description;
    private IncidentSeverity severity;
    private IncidentStatus status = IncidentStatus.OPEN;
    private String reporterName;
    private String reporterEmail;
    private String assignedTechnician;
    private Instant createdAt = Instant.now();
    private Instant resolvedAt;

    public Incident() {
    }

    public Incident(String resourceId, String title, String description, IncidentSeverity severity,
                     String reporterName, String reporterEmail) {
        this.resourceId = resourceId;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.reporterName = reporterName;
        this.reporterEmail = reporterEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(IncidentSeverity severity) {
        this.severity = severity;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public String getAssignedTechnician() {
        return assignedTechnician;
    }

    public void setAssignedTechnician(String assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
