package com.example.Event_Manager.models.event.controller;

import com.example.Event_Manager.models.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.models.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.models.event.dto.response.EventDTO;
import com.example.Event_Manager.models.event.dto.response.EventSummaryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Validated
@Tag(name = "Event Management", description = "APIs for managing events")
public interface EventApi {
    @Operation(summary = "Create a new event",
            description = "Allows an authenticated user to create a new event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or invalid event date"),
            @ApiResponse(responseCode = "404", description = "Venue, category, or organizer not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate event or event capacity exceeded")
    })
    ResponseEntity<EventDTO> createEvent(@Valid CreateEventDTO createEventDTO);

    @Operation(summary = "Update an existing event",
            description = "Allows updating an existing event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or invalid event date"),
            @ApiResponse(responseCode = "403", description = "Unauthorized to update this event"),
            @ApiResponse(responseCode = "404", description = "Event, venue, category, or organizer not found"),
            @ApiResponse(responseCode = "409", description = "Event already started or duplicate event")
    })
    ResponseEntity<EventDTO> updateEvent(Long id, @Valid UpdateEventDTO updateEventDTO);

    @Operation(summary = "Delete an event",
            description = "Deletes an event by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized to delete this event"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "409", description = "Event already started")
    })
    ResponseEntity<Void> deleteEvent(Long id);

    @Operation(summary = "Get event by ID",
            description = "Retrieves an event by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    ResponseEntity<EventDTO> getEventById(Long id);

    @Operation(summary = "Get all events",
            description = "Retrieves paginated all events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No events found")
    })
    ResponseEntity<Page<EventDTO>> getAllEvents(Pageable pageable);

    @Operation(summary = "Get events by category",
            description = "Retrieves paginated events by category ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found or no events found for this category")
    })
    ResponseEntity<Page<EventDTO>> getEventsByCategory(Long categoryId, Pageable pageable);

    @Operation(summary = "Get events by venue",
            description = "Retrieves paginated events by venue ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Venue not found or no events found for this venue")
    })
    ResponseEntity<Page<EventDTO>> getEventsByVenue(Long venueId, Pageable pageable);

    @Operation(summary = "Get events by date range",
            description = "Retrieves paginated events within a date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "404", description = "No events found in the specified date range")
    })
    ResponseEntity<Page<EventDTO>> getEventsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Operation(summary = "Search events by name",
            description = "Searches paginated events by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No events found matching the search criteria")
    })
    ResponseEntity<Page<EventDTO>> searchEventsByName(String name, Pageable pageable);

    @Operation(summary = "Get events by organizer ID",
            description = "Retrieves paginated events by organizer ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Organizer not found or no events found for this organizer")
    })
    ResponseEntity<Page<EventDTO>> getEventsByOrganizer(Long organizerId, Pageable pageable);

    @Operation(summary = "Get event summary",
            description = "Retrieves a summary of the event by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event summary retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found or event summary not available")
    })
    ResponseEntity<EventSummaryDTO> getEventSummary(Long eventId);

    @Operation(summary = "Get events by organizer name",
            description = "Retrieves paginated events by organizer name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No events found for the given organizer name")
    })
    ResponseEntity<Page<EventDTO>> getEventsByOrganizerName(String organizerName, Pageable pageable);
}