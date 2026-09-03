package com.niluverse.uninex.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest
class ResourceRepositoryTest {

    @Autowired
    private ResourceRepository repository;

    @Test
    void save_find_update_delete_roundTrip() {
        Resource resource = new Resource(
            "Main Auditorium", ResourceType.HALL, "Big hall for events", 200,
            List.of("Projector", "AC"), List.of()
        );

        Resource saved = repository.save(resource);
        assertThat(saved.getId()).isNotBlank();

        Resource found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Main Auditorium");
        assertThat(found.getCapacity()).isEqualTo(200);

        found.setCapacity(250);
        repository.save(found);
        Resource afterUpdate = repository.findById(saved.getId()).orElseThrow();
        assertThat(afterUpdate.getCapacity()).isEqualTo(250);

        repository.deleteById(saved.getId());
        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}
