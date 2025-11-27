package com.example.Event_Manager.models.review.service;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.event.validation.EventValidation;
import com.example.Event_Manager.models.review.Review;
import com.example.Event_Manager.models.review.dto.request.CreateReviewDTO;
import com.example.Event_Manager.models.review.dto.request.UpdateReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewSummaryDTO;
import com.example.Event_Manager.models.review.exceptions.ReviewNotFoundException;
import com.example.Event_Manager.models.review.mapper.ReviewMapper;
import com.example.Event_Manager.models.review.repository.ReviewRepository;
import com.example.Event_Manager.models.review.validation.ReviewValidation;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.models.user.validation.UserValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final ReviewMapper reviewMapper;

    private final UserValidation userValidation;
    private final ReviewValidation reviewValidation;
    private final EventValidation eventValidation;

    @Override
    @Transactional
    public ReviewDTO createReview(CreateReviewDTO review, Long userId) {
        Event event = eventRepository.findById(review.eventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Review entity = reviewMapper.toEntity(review, event, user);
        Review saved = reviewRepository.save(entity);

        return reviewMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(Long reviewId, UpdateReviewDTO reviewRequest, Long userId) {
        reviewValidation.checkIfRequestNotNull(reviewRequest);
        reviewValidation.checkIfIdValid(reviewId);
        Review review = reviewRepository.getReviewById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        reviewValidation.checkIfObjectExist(review);

        reviewMapper.updateEntity(review, reviewRequest);
        Review updated = reviewRepository.save(review);
        return reviewMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        reviewValidation.checkIfRequestNotNull(reviewId);
        reviewValidation.checkIfIdValid(reviewId);
        userValidation.checkIfIdValid(userId);

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        reviewRepository.deleteById(reviewId);
    }

    @Override
    public List<ReviewDTO> getReviewsForEvent(Long eventId) {
        eventValidation.checkIfIdValid(eventId);
        var reviews = reviewRepository.findByEventId(eventId);
        if (reviews == null || reviews.isEmpty()) {
            throw new ReviewNotFoundException("No reviews found for event with id: " + eventId);
        }
        return reviews.stream()
                .map(reviewMapper::toDTO)
                .toList();
    }

    @Override
    public ReviewSummaryDTO getEventReviewSummary(Long eventId) {
        eventValidation.checkIfIdValid(eventId);

        var reviews = reviewRepository.findByEventId(eventId);
        if (reviews == null || reviews.isEmpty()) {
            throw new ReviewNotFoundException("No reviews summary found for event with id: " + eventId);
        }

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        int totalReviews = reviews.size();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(reviewMapper::toDTO)
                .toList();

        Map<Event, List<ReviewDTO>> eventReviewsMap = Map.of(event, reviewDTOs);

        return new ReviewSummaryDTO(
                event.getId(),
                event.getName(),
                averageRating,
                totalReviews,
                eventReviewsMap
        );
    }
}
