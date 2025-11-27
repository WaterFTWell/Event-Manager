package com.example.Event_Manager.models.venue.controller;

import com.example.Event_Manager.models.venue.dto.request.CreateVenueDTO;
import com.example.Event_Manager.models.venue.dto.request.UpdateVenueDTO;
import com.example.Event_Manager.models.venue.dto.response.VenueDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Tag(name = "Venue Management", description = "APIs for managing venues")
public interface VenueApi {
    @Operation(summary = "Get all venues",
            description = "Retrieves all venues with optional filtering by name and cities, with pagination support.")
    @ApiResponse(responseCode = "200", description = "Venues retrieved successfully")
    ResponseEntity<Page<VenueDTO>> getAll(
            @Parameter(description = "Filter venues by name (case-insensitive partial match)")
            String name,
            @Parameter(description = "Filter venues by city IDs (comma-separated, e.g., 1,2)")
            List<Long> cities,
            @Parameter(description = "Pagination parameters (page, size, sort)")
            Pageable pageable
    );

    @Operation(summary = "Get venue by ID",
            description = "Retrieves a venue by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venue found"),
            @ApiResponse(responseCode = "404", description = "Venue not found")
    })
    ResponseEntity<VenueDTO> getById(Long id);

    @Operation(summary = "Create a new venue",
            description = "Allows an authenticated user to create a new venue.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venue created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<VenueDTO> create(CreateVenueDTO createVenueDTO);

    @Operation(summary = "Update an existing venue",
            description = "Allows updating an existing venue.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venue updated successfully"),
            @ApiResponse(responseCode = "404", description = "Venue not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<VenueDTO> update(Long id, UpdateVenueDTO updateVenueDTO);

    @Operation(summary = "Delete a venue",
            description = "Deletes a venue by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Venue deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Venue not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<Void> delete(Long id);
}

