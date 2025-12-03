package com.example.Event_Manager.review.service;

import com.example.Event_Manager.event.Event;
import com.example.Event_Manager.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.event.repository.EventRepository;
import com.example.Event_Manager.review.Review;
import com.example.Event_Manager.review.dto.request.CreateReviewDTO;
import com.example.Event_Manager.review.dto.request.UpdateReviewDTO;
import com.example.Event_Manager.review.dto.response.ReviewDTO;
import com.example.Event_Manager.review.dto.response.ReviewSummaryDTO;
import com.example.Event_Manager.review.exceptions.ReviewNotFoundException;
import com.example.Event_Manager.review.mapper.ReviewMapper;
import com.example.Event_Manager.review.repository.ReviewRepository;
import com.example.Event_Manager.review.validation.ReviewValidation;
import com.example.Event_Manager.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;

    private final ReviewMapper reviewMapper;

    private final ReviewValidation reviewValidation;

    @Override
    @Transactional
    public ReviewDTO createReview(CreateReviewDTO review, User user) {

        Event event = eventRepository.findById(review.eventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        Review entity = reviewMapper.toEntity(review, event, user);
        Review saved = reviewRepository.save(entity);

        return reviewMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(Long reviewId, UpdateReviewDTO reviewRequest, User user) {
        Review review = reviewRepository.getReviewById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        reviewMapper.updateEntity(review, reviewRequest);
        Review updated = reviewRepository.save(review);
        return reviewMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.getReviewById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public Page<ReviewDTO> getReviewsForEvent(Long eventId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByEventId(eventId, pageable);
        reviewValidation.checkIfReviewsEmpty(reviews);
        return reviews.map(reviewMapper::toDTO);
    }

    @Override
    public ReviewSummaryDTO getEventReviewSummary(Long eventId) {
        var eventReviews = reviewRepository.findByEventId(eventId);
        reviewValidation.checkIfReviewsListEmpty(eventReviews);

        double averageRating = eventReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        int totalReviews = eventReviews.size();

        List<ReviewDTO> reviewDTOs = eventReviews.stream()
                .map(reviewMapper::toDTO)
                .toList();

        return new ReviewSummaryDTO(
                eventId,
                eventReviews.getFirst().getEvent().getName(),
                averageRating,
                totalReviews,
                reviewDTOs
        );
    }
}
