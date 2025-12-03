package com.example.Event_Manager.unit.review;

import com.example.Event_Manager.event.Event;
import com.example.Event_Manager.event.repository.EventRepository;
import com.example.Event_Manager.event.validation.EventValidation;
import com.example.Event_Manager.review.Review;
import com.example.Event_Manager.review.dto.response.ReviewDTO;
import com.example.Event_Manager.review.exceptions.ReviewsNotFoundException;
import com.example.Event_Manager.review.mapper.ReviewMapper;
import com.example.Event_Manager.review.repository.ReviewRepository;
import com.example.Event_Manager.review.service.ReviewService;
import com.example.Event_Manager.review.validation.ReviewValidation;
import com.example.Event_Manager.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    private EventRepository eventRepository;

    @Mock
    private ReviewValidation reviewValidation;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private EventValidation eventValidation;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("Should return a page of reviews for a valid event ID")
    void getReviewsForEvent_shouldSucceed_whenReviewsExist() {
        // Given
        Long eventId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Event event = Event.builder().id(eventId).name("Koncert").build();
        User user = User.builder().id(1L).firstName("Doktor").lastName("Makaljer").build();
        LocalDateTime now = LocalDateTime.now();

        Review review1 = Review.builder()
                .id(101L)
                .event(event)
                .user(user)
                .rating(8)
                .comment("Super!")
                .createdAt(now)
                .build();
        Review review2 = Review.builder()
                .id(102L)
                .event(event)
                .user(user)
                .rating(7)
                .comment("Dobry.")
                .createdAt(now.minusHours(1))
                .build();

        List<Review> reviewsFromRepo = List.of(review1, review2);
        Page<Review> reviewPage = new PageImpl<>(reviewsFromRepo, pageable, reviewsFromRepo.size());

        ReviewDTO reviewDTO1 = new ReviewDTO(101L, eventId, "Koncert", 1L,
                "Doktor Makaljer", 8, "Super!", now);
        ReviewDTO reviewDTO2 = new ReviewDTO(102L, eventId, "Koncert", 1L,
                "Doktor Makaljer", 7, "Dobry.", now.minusHours(1));

        // doNothing().when(eventValidation).checkIfIdValid(eventId); // USUNIĘTE
        // when(eventRepository.findById(eventId)).thenReturn(Optional.of(event)); // USUNIĘTE
        // doNothing().when(eventValidation).checkIfObjectExist(event); // USUNIĘTE
        when(reviewRepository.findByEventId(eventId, pageable)).thenReturn(reviewPage);
        when(reviewMapper.toDTO(review1)).thenReturn(reviewDTO1);
        when(reviewMapper.toDTO(review2)).thenReturn(reviewDTO2);
        doNothing().when(reviewValidation).checkIfReviewsEmpty(reviewPage);

        // When
        Page<ReviewDTO> result = reviewService.getReviewsForEvent(eventId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(reviewDTO1.comment(), result.getContent().get(0).comment());
        assertEquals(reviewDTO2.rating(), result.getContent().get(1).rating());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        verify(reviewRepository).findByEventId(eventId, pageable);
        verify(reviewMapper, times(2)).toDTO(any(Review.class));
        verify(reviewValidation).checkIfReviewsEmpty(reviewPage);
    }

    @Test
    @DisplayName("Should return correct page when requesting second page")
    void getReviewsForEvent_shouldReturnSecondPage_whenPageableIsSecondPage() {
        // Given
        Long eventId = 1L;
        Pageable pageable = PageRequest.of(1, 5);

        Event event = Event.builder().id(eventId).name("Koncert").build();
        User user = User.builder().id(1L).firstName("Doktor").lastName("Makaljer").build();
        LocalDateTime now = LocalDateTime.now();

        Review review = Review.builder()
                .id(106L)
                .event(event)
                .user(user)
                .rating(9)
                .comment("Świetny!")
                .createdAt(now)
                .build();

        List<Review> reviewsFromRepo = List.of(review);
        Page<Review> reviewPage = new PageImpl<>(reviewsFromRepo, pageable, 6);

        ReviewDTO reviewDTO = new ReviewDTO(106L, eventId, "Koncert", 1L,
                "Doktor Makaljer", 9, "Świetny!", now);

        when(reviewRepository.findByEventId(eventId, pageable)).thenReturn(reviewPage);
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);
        doNothing().when(reviewValidation).checkIfReviewsEmpty(reviewPage);

        // When
        Page<ReviewDTO> result = reviewService.getReviewsForEvent(eventId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(6, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getNumber());
        assertEquals(2, result.getTotalPages());
        assertFalse(result.hasNext());
        assertTrue(result.hasPrevious());

        verify(reviewRepository).findByEventId(eventId, pageable);
        verify(reviewValidation).checkIfReviewsEmpty(reviewPage);
    }

    @Test
    @DisplayName("Should throw ReviewsNotFoundException when no reviews are found")
    void getReviewsForEvent_shouldThrowException_whenNoReviewsFound() {
        // Given
        Long eventId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        Event event = Event.builder().id(eventId).name("Empty Event").build();
        Page<Review> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(reviewRepository.findByEventId(eventId, pageable)).thenReturn(emptyPage);
        doThrow(new ReviewsNotFoundException("No reviews found for event with id: " + eventId))
                .when(reviewValidation).checkIfReviewsEmpty(emptyPage);

        // When & Then
        ReviewsNotFoundException exception = assertThrows(ReviewsNotFoundException.class, () -> {
            reviewService.getReviewsForEvent(eventId, pageable);
        });

        assertEquals("No reviews found for event with id: " + eventId, exception.getMessage());

        verify(reviewRepository).findByEventId(eventId, pageable);
        verify(reviewValidation).checkIfReviewsEmpty(emptyPage);
        verify(reviewMapper, never()).toDTO(any(Review.class));
    }

    @Test
    @DisplayName("Should throw ReviewsNotFoundException when repository returns null")
    void getReviewsForEvent_shouldThrowException_whenRepositoryReturnsNull() {
        // Given
        Long eventId = 3L;
        Pageable pageable = PageRequest.of(0, 10);

        when(reviewRepository.findByEventId(eventId, pageable)).thenReturn(null);
        doThrow(new ReviewsNotFoundException("No reviews found for event with id: " + eventId))
                .when(reviewValidation).checkIfReviewsEmpty(null);

        // When & Then
        ReviewsNotFoundException exception = assertThrows(ReviewsNotFoundException.class, () -> {
            reviewService.getReviewsForEvent(eventId, pageable);
        });

        assertEquals("No reviews found for event with id: " + eventId, exception.getMessage());

        verify(reviewRepository).findByEventId(eventId, pageable);
        verify(reviewValidation).checkIfReviewsEmpty(null);
    }


    @Test
    @DisplayName("Should handle single review on page")
    void getReviewsForEvent_shouldSucceed_whenSingleReviewExists() {
        // Given
        Long eventId = 4L;
        Pageable pageable = PageRequest.of(0, 10);

        Event event = Event.builder().id(eventId).name("Wystawa").build();
        User user = User.builder().id(2L).firstName("Anna").lastName("Kowalska").build();
        LocalDateTime now = LocalDateTime.now();

        Review review = Review.builder()
                .id(201L)
                .event(event)
                .user(user)
                .rating(10)
                .comment("Fenomenalne!")
                .createdAt(now)
                .build();

        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        ReviewDTO reviewDTO = new ReviewDTO(201L, eventId, "Wystawa", 2L,
                "Anna Kowalska", 10, "Fenomenalne!", now);

        when(reviewRepository.findByEventId(eventId, pageable)).thenReturn(reviewPage);
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);
        doNothing().when(reviewValidation).checkIfReviewsEmpty(reviewPage);

        // When
        Page<ReviewDTO> result = reviewService.getReviewsForEvent(eventId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        assertFalse(result.hasNext());
        assertFalse(result.hasPrevious());

        verify(reviewRepository).findByEventId(eventId, pageable);
        verify(reviewMapper).toDTO(review);
        verify(reviewValidation).checkIfReviewsEmpty(reviewPage);
    }
}