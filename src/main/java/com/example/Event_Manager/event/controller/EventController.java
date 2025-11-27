package com.example.Event_Manager.event.controller;

import com.example.Event_Manager._util.annotations.IsOrganizer;
import com.example.Event_Manager.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.event.dto.response.EventDTO;
import com.example.Event_Manager.event.dto.response.EventSummaryDTO;
import com.example.Event_Manager.event.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
            @PathVariable("id") @Positive(message = "Id should be positive") Long id,
            @Valid @RequestBody UpdateEventDTO updateEventDTO
    ) {
        return ResponseEntity.ok(eventService.updateEvent(id, updateEventDTO));
    }

    @DeleteMapping("/{id}")
    @IsOrganizer
    public ResponseEntity<Void> deleteEvent(
            @PathVariable("id") @Positive(message = "Id should be positive") Long id
    ) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(
            @PathVariable("id") @Positive(message = "Id should be positive") Long id
    ) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping
    public ResponseEntity<Page<EventDTO>> getAllEvents(
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.getAllEvents(pageable));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<EventDTO>> getEventsByCategory(
            @PathVariable("categoryId") @Positive(message = "Id should be positive") Long categoryId,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.getEventsByCategory(categoryId, pageable));
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<Page<EventDTO>> getEventsByVenue(
            @PathVariable("venueId") @Positive(message = "Id should be positive") Long venueId,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.getEventsByVenue(venueId, pageable));
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<EventDTO>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.getEventsByDateRange(start, end, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<EventDTO>> searchEventsByName(
            @RequestParam String name,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.searchEventsByName(name, pageable));
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<Page<EventDTO>> getEventsByOrganizer(
            @PathVariable("organizerId") @Positive(message = "Id should be positive") Long organizerId,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(organizerId, pageable));
    }

    @GetMapping("/{eventId}/summary")
    public ResponseEntity<EventSummaryDTO> getEventSummary(
            @PathVariable("eventId") @Positive(message = "Id should be positive") Long eventId
    ) {
        return ResponseEntity.ok(eventService.getEventSummary(eventId));
    }

    @GetMapping("/organizer/name/{organizerName}")
    public ResponseEntity<Page<EventDTO>> getEventsByOrganizerName(
            @PathVariable @NotBlank(message = "Organizer name should not be empty") String organizerName,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(organizerName, pageable));
    }
}