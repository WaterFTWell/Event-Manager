package com.example.Event_Manager.unit.review;

import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.event.validation.EventValidation;
import com.example.Event_Manager.models.review.Review;
import com.example.Event_Manager.models.review.dto.response.ReviewDTO;
import com.example.Event_Manager.models.review.exceptions.ReviewsNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        doNothing().when(eventValidation).checkIfObjectExist(event);
        when(reviewRepository.findByEventId(eventId, pageable)).thenReturn(reviewPage);
        when(reviewMapper.toDTO(review1)).thenReturn(reviewDTO1);
        when(reviewMapper.toDTO(review2)).thenReturn(reviewDTO2);

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

        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findById(eventId);
        verify(eventValidation).checkIfObjectExist(event);
        verify(reviewRepository).findByEventId(eventId, pageable);
        verify(reviewMapper, times(2)).toDTO(any(Review.class));
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

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        doNothing().when(eventValidation).checkIfObjectExist(event);
        when(reviewRepository.findByEventId(eventId, pageable)).thenReturn(reviewPage);
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);

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

        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findById(eventId);
        verify(eventValidation).checkIfObjectExist(event);
        verify(reviewRepository).findByEventId(eventId, pageable);
    }

    @Test
    @DisplayName("Should throw ReviewsNotFoundException when no reviews are found")
    void getReviewsForEvent_shouldThrowException_whenNoReviewsFound() {
        // Given
        Long eventId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        Event event = Event.builder().id(eventId).name("Empty Event").build();
        Page<Review> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        doNothing().when(eventValidation).checkIfObjectExist(event);
        when(reviewRepository.findByEventId(eventId, pageable)).thenReturn(emptyPage);

        // When & Then
        ReviewsNotFoundException exception = assertThrows(ReviewsNotFoundException.class, () -> {
            reviewService.getReviewsForEvent(eventId, pageable);
        });

        assertEquals("No reviews found for event with id: " + eventId, exception.getMessage());

        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findById(eventId);
        verify(eventValidation).checkIfObjectExist(event);
        verify(reviewRepository).findByEventId(eventId, pageable);
        verify(reviewMapper, never()).toDTO(any(Review.class));
    }

    @Test
    @DisplayName("Should throw ReviewsNotFoundException when repository returns null")
    void getReviewsForEvent_shouldThrowException_whenRepositoryReturnsNull() {
        // Given
        Long eventId = 3L;
        Pageable pageable = PageRequest.of(0, 10);
        Event event = Event.builder().id(eventId).name("Null Event").build();

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        doNothing().when(eventValidation).checkIfObjectExist(event);
        when(reviewRepository.findByEventId(eventId, pageable)).thenReturn(null);

        // When & Then
        ReviewsNotFoundException exception = assertThrows(ReviewsNotFoundException.class, () -> {
            reviewService.getReviewsForEvent(eventId, pageable);
        });

        assertEquals("No reviews found for event with id: " + eventId, exception.getMessage());

        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findById(eventId);
        verify(eventValidation).checkIfObjectExist(event);
        verify(reviewRepository).findByEventId(eventId, pageable);
    }

    @Test
    @DisplayName("Should throw EventNotFoundException for invalid event ID")
    void getReviewsForEvent_shouldThrowException_forInvalidEventId() {
        // Given
        Long invalidEventId = 0L;
        Pageable pageable = PageRequest.of(0, 10);

        doThrow(new EventNotFoundException("ID must be greater than 0."))
                .when(eventValidation).checkIfIdValid(invalidEventId);

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            reviewService.getReviewsForEvent(invalidEventId, pageable);
        });

        assertEquals("ID must be greater than 0.", exception.getMessage());

        verify(eventValidation).checkIfIdValid(invalidEventId);
        verify(eventRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).findByEventId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Should throw EventNotFoundException when event is not found")
    void getReviewsForEvent_shouldThrowException_whenEventNotFound() {
        // Given
        Long eventId = 999L;
        Pageable pageable = PageRequest.of(0, 10);

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            reviewService.getReviewsForEvent(eventId, pageable);
        });

        assertEquals("Event not found", exception.getMessage());

        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findById(eventId);
        verify(reviewRepository, never()).findByEventId(anyLong(), any(Pageable.class));
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

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        doNothing().when(eventValidation).checkIfObjectExist(event);
        when(reviewRepository.findByEventId(eventId, pageable)).thenReturn(reviewPage);
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);

        // When
        Page<ReviewDTO> result = reviewService.getReviewsForEvent(eventId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        assertFalse(result.hasNext());
        assertFalse(result.hasPrevious());

        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findById(eventId);
        verify(eventValidation).checkIfObjectExist(event);
        verify(reviewRepository).findByEventId(eventId, pageable);
        verify(reviewMapper).toDTO(review);
    }
}