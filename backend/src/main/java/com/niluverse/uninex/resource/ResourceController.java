package com.niluverse.uninex.resource;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService service;

    public ResourceController(ResourceService service) {
        this.service = service;
    }

    @GetMapping
    public List<Resource> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Resource get(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Resource create(@Valid @RequestBody ResourceRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public Resource update(@PathVariable String id, @Valid @RequestBody ResourceRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
