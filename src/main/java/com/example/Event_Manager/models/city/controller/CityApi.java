package com.example.Event_Manager.models.city.controller;

import com.example.Event_Manager.models.city.dto.request.CreateCityDTO;
import com.example.Event_Manager.models.city.dto.request.UpdateCityDTO;
import com.example.Event_Manager.models.city.dto.response.CityDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Tag(name = "City Management", description = "APIs for managing cities")
public interface CityApi {
    @Operation(summary = "Get all cities",
            description = "Retrieves all cities with optional filtering by name and countries.")
    @ApiResponse(responseCode = "200", description = "Cities retrieved successfully")
    ResponseEntity<List<CityDTO>> getAll(
            @Parameter(description = "Filter cities by name (case-insensitive partial match)")
            String name,
            @Parameter(description = "Filter cities by country codes (comma-separated, e.g., PL,DE)")
            List<String> countries
    );

    @Operation(summary = "Get city by ID",
            description = "Retrieves a city by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "City found"),
            @ApiResponse(responseCode = "404", description = "City not found")
    })
    ResponseEntity<CityDTO> getById(Long id);

    @Operation(summary = "Create a new city",
            description = "Allows an authenticated user to create a new city.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "City created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<CityDTO> create(@Valid CreateCityDTO createCityDTO);

    @Operation(summary = "Update an existing city",
            description = "Allows updating an existing city.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "City updated successfully"),
            @ApiResponse(responseCode = "404", description = "City not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<CityDTO> update(Long id, @Valid UpdateCityDTO updateCityDTO);

    @Operation(summary = "Delete a city",
            description = "Deletes a city by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "City deleted successfully"),
            @ApiResponse(responseCode = "404", description = "City not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<Void> delete(Long id);
}

