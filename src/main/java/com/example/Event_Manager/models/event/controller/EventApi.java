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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@Tag(name = "Event Management", description = "APIs for managing events")
public interface EventApi {
    @Operation(summary = "Create a new event",
            description = "Allows an authenticated user to create a new event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<EventDTO> createEvent(@Valid CreateEventDTO createEventDTO);

    @Operation(summary = "Update an existing event",
            description = "Allows updating an existing event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    ResponseEntity<EventDTO> updateEvent(Long id, @Valid UpdateEventDTO updateEventDTO);

    @Operation(summary = "Delete an event",
            description = "Deletes an event by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
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
            description = "Retrieves all events.")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    ResponseEntity<List<EventDTO>> getAllEvents();

    @Operation(summary = "Get events by category",
            description = "Retrieves events by category ID.")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    ResponseEntity<List<EventDTO>> getEventsByCategory(Long categoryId);

    @Operation(summary = "Get events by venue",
            description = "Retrieves events by venue ID.")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    ResponseEntity<List<EventDTO>> getEventsByVenue(Long venueId);

    @Operation(summary = "Get events by date range",
            description = "Retrieves events within a date range.")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    ResponseEntity<List<EventDTO>> getEventsByDateRange(LocalDateTime start, LocalDateTime end);

    @Operation(summary = "Search events by name",
            description = "Searches events by name.")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    ResponseEntity<List<EventDTO>> searchEventsByName(String name);

    @Operation(summary = "Get events by organizer",
            description = "Retrieves events by organizer ID.")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    ResponseEntity<List<EventDTO>> getEventsByOrganizer(Long organizerId);

    @Operation(summary = "Get event summary",
            description = "Retrieves a summary of the event by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event summary retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    ResponseEntity<EventSummaryDTO> getEventSummary(Long eventId);

    @Operation(summary = "Get events by organizer name",
            description = "Retrieves events by organizer name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No events found for the given organizer name")
    })
    ResponseEntity<List<EventDTO>> getEventsByOrganizer(String organizerName);
}
