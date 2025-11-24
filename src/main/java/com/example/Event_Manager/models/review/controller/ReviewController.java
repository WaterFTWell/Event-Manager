package com.example.Event_Manager.models.review.controller;

import com.example.Event_Manager.models._util.annotations.IsAttendee;
import com.example.Event_Manager.models.review.dto.request.CreateReviewDTO;
import com.example.Event_Manager.models.review.dto.request.UpdateReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewSummaryDTO;
import com.example.Event_Manager.models.review.service.IReviewService;
import com.example.Event_Manager.models.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin
@Validated
public class ReviewController implements ReviewApi {

    private final IReviewService reviewService;

    @PostMapping
    @IsAttendee
    public ResponseEntity<ReviewDTO> createReview(
            @Valid @RequestBody CreateReviewDTO createReviewDTO,
            @AuthenticationPrincipal User user
    ) {
        ReviewDTO response = reviewService.createReview(createReviewDTO, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{reviewId}")
    @IsAttendee
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewDTO updateReviewDTO,
            @AuthenticationPrincipal User user
    ) {
        ReviewDTO response = reviewService.updateReview(reviewId, updateReviewDTO, user.getId());
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{reviewId}")
    @IsAttendee
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User user
    ) {
        reviewService.deleteReview(reviewId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsForEvent(
            @PathVariable Long eventId
    ) {
        List<ReviewDTO> response = reviewService.getReviewsForEvent(eventId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/event/{eventId}/summary")
    public ResponseEntity<ReviewSummaryDTO> getEventReviewSummary(
            @PathVariable Long eventId
    ) {
        ReviewSummaryDTO response = reviewService.getEventReviewSummary(eventId);
        return ResponseEntity.ok(response);
    }

}