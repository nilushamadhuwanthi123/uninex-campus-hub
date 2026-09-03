package com.niluverse.uninex.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository repository;

    private ResourceService service;

    @BeforeEach
    void setUp() {
        service = new ResourceService(repository);
    }

    @Test
    void findById_returnsResource_whenPresent() {
        Resource resource = new Resource("Main Auditorium", ResourceType.HALL, "Big hall", 200,
            List.of("Projector"), List.of());
        resource.setId("r1");
        when(repository.findById("r1")).thenReturn(Optional.of(resource));

        Resource found = service.findById("r1");

        assertThat(found.getName()).isEqualTo("Main Auditorium");
    }

    @Test
    void findById_throwsNotFound_whenMissing() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("missing"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("missing");
    }

    @Test
    void create_buildsResourceFromRequest_andSaves() {
        ResourceRequest request = new ResourceRequest(
            "Lab 3", ResourceType.LAB, "CS lab", 40, List.of("PCs", "Whiteboard"), List.of()
        );
        when(repository.save(any(Resource.class))).thenAnswer(inv -> inv.getArgument(0));

        Resource created = service.create(request);

        assertThat(created.getName()).isEqualTo("Lab 3");
        assertThat(created.getType()).isEqualTo(ResourceType.LAB);
        assertThat(created.getCapacity()).isEqualTo(40);
        verify(repository, times(1)).save(any(Resource.class));
    }

    @Test
    void update_appliesRequestFields_toExistingResource() {
        Resource existing = new Resource("Old Name", ResourceType.ROOM, "old", 10,
            List.of(), List.of());
        existing.setId("r2");
        when(repository.findById("r2")).thenReturn(Optional.of(existing));
        when(repository.save(any(Resource.class))).thenAnswer(inv -> inv.getArgument(0));

        ResourceRequest request = new ResourceRequest(
            "New Name", ResourceType.ROOM, "updated", 25, List.of("AC"), List.of()
        );
        Resource updated = service.update("r2", request);

        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getCapacity()).isEqualTo(25);
        assertThat(updated.getDescription()).isEqualTo("updated");
    }

    @Test
    void delete_removesResource_whenItExists() {
        when(repository.existsById("r3")).thenReturn(true);

        service.delete("r3");

        verify(repository, times(1)).deleteById("r3");
    }

    @Test
    void delete_throwsNotFound_whenResourceDoesNotExist() {
        when(repository.existsById("ghost")).thenReturn(false);

        assertThatThrownBy(() -> service.delete("ghost"))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }
}
