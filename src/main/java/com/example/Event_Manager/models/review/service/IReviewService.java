package com.example.Event_Manager.models.review.service;

import com.example.Event_Manager.models.review.dto.request.CreateReviewDTO;
import com.example.Event_Manager.models.review.dto.request.UpdateReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewSummaryDTO;

import java.util.List;

public interface IReviewService {
    ReviewDTO createReview(CreateReviewDTO review, Long userId);
    ReviewDTO updateReview(Long reviewId, UpdateReviewDTO review, Long userId);
    void deleteReview(Long reviewId, Long userId);
    List<ReviewDTO> getReviewsForEvent(Long eventId);
    ReviewSummaryDTO getEventReviewSummary(Long eventId);
}
