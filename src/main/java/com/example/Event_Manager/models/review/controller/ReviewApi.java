package com.example.Event_Manager.models.review.controller;

import com.example.Event_Manager.models.review.dto.request.CreateReviewDTO;
import com.example.Event_Manager.models.review.dto.request.UpdateReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewSummaryDTO;
import com.example.Event_Manager.models.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Tag(name = "Review Management", description = "APIs for managing event reviews")
public interface ReviewApi {

    @Operation(summary = "Create a new review for an event",
                description = "Allows an authenticated user to create a review for a specific event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Duplicate review detected")
    })
    ResponseEntity<ReviewDTO> createReview(
            @Valid CreateReviewDTO createReviewDTO,
            User user
    );

    @Operation(summary = "Update an existing review",
                description = "Allows an authenticated user to update their existing review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized to update this review")
    })
     ResponseEntity<ReviewDTO> updateReview(
            Long reviewId,
            @Valid UpdateReviewDTO updateReviewDTO,
            User user
    );

    @Operation(summary = "Delete a review",
                description = "Allows an authenticated user to delete their review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized to delete this review")
    })
     ResponseEntity<Void> deleteReview(
             Long reviewId,
             User user
    );

    @Operation(summary = "Get all reviews for an event",
                description = "Retrieves all reviews associated with a specific event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
     ResponseEntity<List<ReviewDTO>> getReviewsForEvent(
             Long eventId
    );

    @Operation(summary = "Get review summary for an event",
                description = "Retrieves a summary of reviews for a specific event, " +
                        "including average rating and total number of reviews.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review summary retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
     ResponseEntity<ReviewSummaryDTO> getEventReviewSummary(
             Long eventId
    );
}
