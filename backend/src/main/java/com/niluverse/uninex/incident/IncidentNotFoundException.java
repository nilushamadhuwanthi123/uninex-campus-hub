package com.niluverse.uninex.incident;

public class IncidentNotFoundException extends RuntimeException {
    public IncidentNotFoundException(String id) {
        super("Incident not found: " + id);
    }
}
