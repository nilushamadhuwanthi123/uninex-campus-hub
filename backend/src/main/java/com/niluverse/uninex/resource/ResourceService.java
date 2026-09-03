package com.niluverse.uninex.resource;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {

    private final ResourceRepository repository;

    public ResourceService(ResourceRepository repository) {
        this.repository = repository;
    }

    public List<Resource> findAll() {
        return repository.findAll();
    }

    public Resource findById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Resource create(ResourceRequest request) {
        Resource resource = new Resource(
            request.name(),
            request.type(),
            request.description(),
            request.capacity(),
            request.facilities(),
            request.seats()
        );
        return repository.save(resource);
    }

    public Resource update(String id, ResourceRequest request) {
        Resource existing = findById(id);
        existing.setName(request.name());
        existing.setType(request.type());
        existing.setDescription(request.description());
        existing.setCapacity(request.capacity());
        existing.setFacilities(request.facilities());
        existing.setSeats(request.seats());
        return repository.save(existing);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
