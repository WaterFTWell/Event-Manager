package com.example.Event_Manager.models.category.controller;

import com.example.Event_Manager.models.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.models.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Tag(name = "Category Management", description = "APIs for managing categories")
public interface CategoryApi {

    @Operation(summary = "Create a new category",
            description = "Creates a new category for events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Category with this name already exists")
    })
    ResponseEntity<CategoryDTO> createCategory(@Valid CreateCategoryDTO createCategoryDTO);

    @Operation(summary = "Update an existing category",
            description = "Updates a category's details by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Another category with this name already exists")
    })
    ResponseEntity<CategoryDTO> updateCategory(Long id, @Valid UpdateCategoryDTO updateCategoryDTO);

    @Operation(summary = "Delete a category",
            description = "Deletes a category by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    ResponseEntity<Void> deleteCategory(Long id);

    @Operation(summary = "Get category by ID",
            description = "Retrieves a category by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    ResponseEntity<CategoryDTO> getCategoryById(Long id);

    @Operation(summary = "Get all categories",
            description = "Retrieves a list of all categories.")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    ResponseEntity<List<CategoryDTO>> getAllCategories();
}