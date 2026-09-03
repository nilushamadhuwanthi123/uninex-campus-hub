package com.niluverse.uninex.incident;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IncidentRepository extends MongoRepository<Incident, String> {
    List<Incident> findByResourceId(String resourceId);
    List<Incident> findByStatus(IncidentStatus status);
}
