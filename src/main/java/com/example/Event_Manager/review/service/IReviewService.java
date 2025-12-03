package com.example.Event_Manager.review.service;

import com.example.Event_Manager.review.dto.request.CreateReviewDTO;
import com.example.Event_Manager.review.dto.request.UpdateReviewDTO;
import com.example.Event_Manager.review.dto.response.ReviewDTO;
import com.example.Event_Manager.review.dto.response.ReviewSummaryDTO;
import com.example.Event_Manager.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReviewService {
    ReviewDTO createReview(CreateReviewDTO review, User user);
    ReviewDTO updateReview(Long reviewId, UpdateReviewDTO review, User user);
    void deleteReview(Long reviewId, User user);
    Page<ReviewDTO> getReviewsForEvent(Long eventId, Pageable pageable);
    ReviewSummaryDTO getEventReviewSummary(Long eventId);
}
