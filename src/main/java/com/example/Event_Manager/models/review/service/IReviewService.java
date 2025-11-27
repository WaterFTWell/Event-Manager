package com.example.Event_Manager.models.review.service;

import com.example.Event_Manager.models.review.dto.request.CreateReviewDTO;
import com.example.Event_Manager.models.review.dto.request.UpdateReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReviewService {
    ReviewDTO createReview(CreateReviewDTO review, Long userId);
    ReviewDTO updateReview(Long reviewId, UpdateReviewDTO review, Long userId);
    void deleteReview(Long reviewId, Long userId);
    Page<ReviewDTO> getReviewsForEvent(Long eventId, Pageable pageable);
    ReviewSummaryDTO getEventReviewSummary(Long eventId);
}
