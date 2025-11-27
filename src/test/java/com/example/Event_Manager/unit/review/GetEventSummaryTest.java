package com.example.Event_Manager.unit.review;

import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.event.validation.EventValidation;
import com.example.Event_Manager.models.review.Review;
import com.example.Event_Manager.models.review.dto.response.ReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewSummaryDTO;
import com.example.Event_Manager.models.review.exceptions.ReviewNotFoundException;
import com.example.Event_Manager.models.review.mapper.ReviewMapper;
import com.example.Event_Manager.models.review.repository.ReviewRepository;
import com.example.Event_Manager.models.review.service.ReviewService;
import com.example.Event_Manager.models.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Get Event Review Summary")
public class GetEventSummaryTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private EventValidation eventValidation;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("Should return event review summary successfully when reviews exist")
    void getEventReviewSummary_shouldSucceed_whenReviewsExist() {
        // Given
        Long eventId = 1L;
        Event event = Event.builder().id(eventId).name("Koncert Rockowy").build();
        User user1 = User.builder().id(1L).firstName("Jan").lastName("Kowalski").build();
        User user2 = User.builder().id(2L).firstName("Anna").lastName("Nowak").build();
        LocalDateTime now = LocalDateTime.now();

        Review review1 = Review.builder().id(101L).event(event).user(user1).rating(8).
                comment("fajen generalnie").createdAt(now).build();
        Review review2 = Review.builder().id(102L).event(event).user(user2).rating(6).
                comment("spoko ale dj wheelitup lepszy na sc").createdAt(now).build();
        List<Review> reviews = List.of(review1, review2);

        ReviewDTO reviewDTO1 = new ReviewDTO(101L, eventId, "Koncert Rockowy",
                1L, "Jan Kowalski", 8, "fajen generalnie", now);
        ReviewDTO reviewDTO2 = new ReviewDTO(102L, eventId, "Koncert Rockowy",
                2L, "Anna Nowak", 6, "spoko ale dj wheelitup lepszy na sc", now);

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(reviewRepository.findByEventId(eventId)).thenReturn(reviews);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(reviewMapper.toDTO(review1)).thenReturn(reviewDTO1);
        when(reviewMapper.toDTO(review2)).thenReturn(reviewDTO2);

        // When
        ReviewSummaryDTO summary = reviewService.getEventReviewSummary(eventId);

        // Then
        assertNotNull(summary);
        assertEquals(eventId, summary.eventId());
        assertEquals("Koncert Rockowy", summary.eventName());
        assertEquals(2, summary.totalReviews());
        assertEquals(7.0, summary.averageRating());
        assertTrue(summary.eventRatings().containsKey(event));
        assertEquals(2, summary.eventRatings().get(event).size());

        verify(eventValidation).checkIfIdValid(eventId);
        verify(reviewRepository).findByEventId(eventId);
        verify(eventRepository).findById(eventId);
        verify(reviewMapper, times(2)).toDTO(any(Review.class));
    }

    @Test
    @DisplayName("Should throw ReviewNotFoundException when no reviews are found for the event")
    void getEventReviewSummary_shouldThrowException_whenNoReviewsFound() {
        // Given
        Long eventId = 2L;
        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(reviewRepository.findByEventId(eventId)).thenReturn(Collections.emptyList());

        // When & Then
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.getEventReviewSummary(eventId);
        });

        assertEquals("No reviews summary found for event with id: " + eventId, exception.getMessage());
        verify(eventRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw EventNotFoundException when reviews exist but event is not found")
    void getEventReviewSummary_shouldThrowException_whenEventIsNotFound() {
        // Given
        Long eventId = 3L;
        Review review = Review.builder().id(103L).rating(5).build();
        List<Review> reviews = List.of(review);

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(reviewRepository.findByEventId(eventId)).thenReturn(reviews);
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            reviewService.getEventReviewSummary(eventId);
        });

        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw EventNotFoundException for invalid event ID")
    void getEventReviewSummary_shouldThrowException_forInvalidEventId() {
        // Given
        Long invalidEventId = -1L;
        doThrow(new EventNotFoundException("ID must be greater than 0."))
                .when(eventValidation).checkIfIdValid(invalidEventId);

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            reviewService.getEventReviewSummary(invalidEventId);
        });

        assertEquals("ID must be greater than 0.", exception.getMessage());
        verify(reviewRepository, never()).findByEventId(anyLong());
    }

    @Test
    @DisplayName("Should correctly calculate average rating for a single review")
    void getEventReviewSummary_shouldCalculateAverageCorrectly_forSingleReview() {
        // Given
        Long eventId = 4L;
        Event event = Event.builder().id(eventId).name("Wystawa Sztuki").build();
        User user = User.builder().id(3L).firstName("Piotr").lastName("Zieliński").build();
        Review review = Review.builder().id(104L).event(event).user(user).rating(10).build();
        ReviewDTO reviewDTO = new ReviewDTO(104L, eventId, "Wystawa Sztuki", 3L,
                "Piotr Zieliński", 10, null, null);

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(reviewRepository.findByEventId(eventId)).thenReturn(List.of(review));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);

        // When
        ReviewSummaryDTO summary = reviewService.getEventReviewSummary(eventId);

        // Then
        assertNotNull(summary);
        assertEquals(1, summary.totalReviews());
        assertEquals(10.0, summary.averageRating());
    }
}