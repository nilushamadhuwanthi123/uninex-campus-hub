package com.niluverse.uninex.booking;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Booking> list(@RequestParam(required = false) String resourceId) {
        return resourceId != null ? service.findByResource(resourceId) : service.findAll();
    }

    @GetMapping("/{id}")
    public Booking get(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@Valid @RequestBody BookingRequest request) {
        return service.create(request);
    }

    @PostMapping("/{id}/cancel")
    public Booking cancel(@PathVariable String id) {
        service.cancel(id);
        return service.findById(id);
    }
}
