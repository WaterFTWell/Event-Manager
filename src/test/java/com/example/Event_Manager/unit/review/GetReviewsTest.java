package com.example.Event_Manager.unit.review;

import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.models.event.validation.EventValidation;
import com.example.Event_Manager.models.review.Review;
import com.example.Event_Manager.models.review.dto.response.ReviewDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Get Reviews For Event")
public class GetReviewsTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private EventValidation eventValidation;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("Should return a list of reviews for a valid event ID")
    void getReviewsForEvent_shouldSucceed_whenReviewsExist() {
        // Given
        Long eventId = 1L;
        Event event = Event.builder().id(eventId).name("Koncert").build();
        User user = User.builder().id(1L).firstName("Doktor").lastName("Makaljer").build();
        LocalDateTime now = LocalDateTime.now();

        Review review1 = Review.builder().id(101L).event(event).user(user).rating(8).comment("Super!").createdAt(now).build();
        Review review2 = Review.builder().id(102L).event(event).user(user).rating(7).comment("Dobry.").createdAt(now.minusHours(1)).build();
        List<Review> reviewsFromRepo = List.of(review1, review2);

        ReviewDTO reviewDTO1 = new ReviewDTO(101L, eventId, "Koncert", 1L,
                "Doktor Makaljer", 8, "Super!", now);
        ReviewDTO reviewDTO2 = new ReviewDTO(102L, eventId, "Koncert", 1L,
                "Doktor Makaljer", 7, "Dobry.", now.minusHours(1));

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(reviewRepository.findByEventId(eventId)).thenReturn(reviewsFromRepo);
        when(reviewMapper.toDTO(review1)).thenReturn(reviewDTO1);
        when(reviewMapper.toDTO(review2)).thenReturn(reviewDTO2);

        // When
        List<ReviewDTO> result = reviewService.getReviewsForEvent(eventId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(reviewDTO1.comment(), result.get(0).comment());
        assertEquals(reviewDTO2.rating(), result.get(1).rating());

        verify(eventValidation).checkIfIdValid(eventId);
        verify(reviewRepository).findByEventId(eventId);
        verify(reviewMapper, times(2)).toDTO(any(Review.class));
    }

    @Test
    @DisplayName("Should throw ReviewNotFoundException when no reviews are found")
    void getReviewsForEvent_shouldThrowException_whenNoReviewsFound() {
        // Given
        Long eventId = 2L;
        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(reviewRepository.findByEventId(eventId)).thenReturn(Collections.emptyList());

        // When & Then
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.getReviewsForEvent(eventId);
        });

        assertEquals("No reviews found for event with id: " + eventId, exception.getMessage());

        verify(reviewMapper, never()).toDTO(any(Review.class));
    }

    @Test
    @DisplayName("Should throw ReviewNotFoundException when repository returns null")
    void getReviewsForEvent_shouldThrowException_whenRepositoryReturnsNull() {
        // Given
        Long eventId = 3L;
        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(reviewRepository.findByEventId(eventId)).thenReturn(null);

        // When & Then
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.getReviewsForEvent(eventId);
        });

        assertEquals("No reviews found for event with id: " + eventId, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw EventNotFoundException for invalid event ID")
    void getReviewsForEvent_shouldThrowException_forInvalidEventId() {
        // Given
        Long invalidEventId = 0L;
        doThrow(new EventNotFoundException("ID must be greater than 0."))
                .when(eventValidation).checkIfIdValid(invalidEventId);

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            reviewService.getReviewsForEvent(invalidEventId);
        });

        assertEquals("ID must be greater than 0.", exception.getMessage());
        verify(reviewRepository, never()).findByEventId(anyLong());
    }
}