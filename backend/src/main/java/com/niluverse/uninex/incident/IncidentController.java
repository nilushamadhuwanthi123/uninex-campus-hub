package com.niluverse.uninex.incident;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Incident> list(@RequestParam(required = false) String resourceId,
                                @RequestParam(required = false) IncidentStatus status) {
        if (resourceId != null) {
            return service.findByResource(resourceId);
        }
        if (status != null) {
            return service.findByStatus(status);
        }
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Incident get(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Incident create(@Valid @RequestBody IncidentRequest request) {
        return service.create(request);
    }

    @PostMapping("/{id}/assign")
    public Incident assign(@PathVariable String id, @RequestBody Map<String, String> body) {
        return service.assignTechnician(id, body.get("technicianName"));
    }

    @PostMapping("/{id}/start")
    public Incident start(@PathVariable String id) {
        return service.startWork(id);
    }

    @PostMapping("/{id}/resolve")
    public Incident resolve(@PathVariable String id) {
        return service.resolve(id);
    }

    @PostMapping("/{id}/close")
    public Incident close(@PathVariable String id) {
        return service.close(id);
    }
}
