package com.example.Event_Manager.models.event.controller;

import com.example.Event_Manager.models._util.annotations.IsOrganizer;
import com.example.Event_Manager.models.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.models.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.models.event.dto.response.EventDTO;
import com.example.Event_Manager.models.event.dto.response.EventSummaryDTO;
import com.example.Event_Manager.models.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin
@Validated
public class EventController implements EventApi {
    private final EventService eventService;

    @PostMapping
    @IsOrganizer
    public ResponseEntity<EventDTO> createEvent(
            @Valid @RequestBody CreateEventDTO createEventDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(createEventDTO));
    }

    @PutMapping("/{id}")
    @IsOrganizer
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventDTO updateEventDTO
    ) {
        return ResponseEntity.ok(eventService.updateEvent(id, updateEventDTO));
    }

    @DeleteMapping("/{id}")
    @IsOrganizer
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id
    ) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<EventDTO>> getEventsByCategory(
            @PathVariable Long categoryId
    ) {
        return ResponseEntity.ok(eventService.getEventsByCategory(categoryId));
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<EventDTO>> getEventsByVenue(
            @PathVariable Long venueId
    ) {
        return ResponseEntity.ok(eventService.getEventsByVenue(venueId));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<EventDTO>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(eventService.getEventsByDateRange(start, end));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEventsByName(
            @RequestParam String name
    ) {
        return ResponseEntity.ok(eventService.searchEventsByName(name));
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<EventDTO>> getEventsByOrganizer(
            @PathVariable Long organizerId
    ) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(organizerId));
    }

    @GetMapping("/{eventId}/summary")
    public ResponseEntity<EventSummaryDTO> getEventSummary(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventSummary(eventId));
    }

    @GetMapping("/organizer/name/{organizerName}")
    public ResponseEntity<List<EventDTO>> getEventsByOrganizer(
            @PathVariable String organizerName) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(organizerName));
    }
}