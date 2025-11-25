package com.example.Event_Manager.models.country.controller;

import com.example.Event_Manager.models.country.dto.request.CreateCountryDTO;
import com.example.Event_Manager.models.country.dto.request.UpdateCountryDTO;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Tag(name = "Country Management", description = "APIs for managing countries")
public interface CountryApi {
    @Operation(summary = "Create a new country",
            description = "Allows an authenticated user to create a new country.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Country created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<CountryDTO> create(@Valid CreateCountryDTO createCountryDTO);

    @Operation(summary = "Update an existing country",
            description = "Allows updating an existing country.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Country updated successfully"),
            @ApiResponse(responseCode = "404", description = "Country not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<CountryDTO> update(String code, @Valid UpdateCountryDTO updateCountryDTO);

    @Operation(summary = "Delete a country",
            description = "Deletes a country by its code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Country deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Country not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<Void> delete(String code);

    @Operation(summary = "Get country by code",
            description = "Retrieves a country by its code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Country found"),
            @ApiResponse(responseCode = "404", description = "Country not found")
    })
    ResponseEntity<CountryDTO> getByCode(String code);

    @Operation(summary = "Get all countries",
            description = "Retrieves all countries.")
    @ApiResponse(responseCode = "200", description = "Countries retrieved successfully")
    ResponseEntity<List<CountryDTO>> getAll();
}
