package com.example.Event_Manager.unit.review;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.review.Review;
import com.example.Event_Manager.models.review.dto.request.CreateReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewDTO;
import com.example.Event_Manager.models.review.mapper.ReviewMapper;
import com.example.Event_Manager.models.review.repository.ReviewRepository;
import com.example.Event_Manager.models.review.service.ReviewService;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Review Creation")
public class CreateReviewTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("Should create review successfully with valid data")
    void createReview_shouldSucceed_whenDataIsValid() {
        // Given
        Long userId = 1L;
        Long eventId = 1L;
        Long categoryId = 1L;
        LocalDateTime now = LocalDateTime.now();

        CreateReviewDTO createDTO = new CreateReviewDTO(eventId, categoryId, 5, "Amazing event!");
        User user = User.builder().id(userId).firstName("Jan").lastName("Kowalski").build();
        Event event = Event.builder().id(eventId).name("Awesome Concert").build();

        Review reviewToSave = Review.builder().user(user).event(event).rating(5).comment("Amazing event!").build();
        Review savedReview = Review.builder().id(1L).user(user).event(event).rating(5).comment("Amazing event!").createdAt(now).build();

        ReviewDTO expectedDTO = new ReviewDTO(1L, eventId, "Awesome Concert", userId,
                "Jan Kowalski", 5, "Amazing event!", now);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reviewMapper.toEntity(createDTO, event, user)).thenReturn(reviewToSave);
        when(reviewRepository.save(reviewToSave)).thenReturn(savedReview);
        when(reviewMapper.toDTO(savedReview)).thenReturn(expectedDTO);

        // When
        ReviewDTO result = reviewService.createReview(createDTO, userId);

        // Then
        assertNotNull(result, "Returned DTO should not be null.");
        assertEquals(expectedDTO.id(), result.id());
        assertEquals(expectedDTO.rating(), result.rating());
        assertEquals(expectedDTO.comment(), result.comment());
        assertEquals(expectedDTO.eventId(), result.eventId());
        assertEquals(expectedDTO.createdAt(), result.createdAt());


        verify(eventRepository).findById(eventId);
        verify(userRepository).findById(userId);
        verify(reviewRepository).save(reviewToSave);
        verify(reviewMapper).toDTO(savedReview);
    }

    @Test
    @DisplayName("Should throw exception when event does not exist")
    void createReview_shouldThrowException_whenEventNotFound() {
        // Given
        Long userId = 1L;
        Long nonExistentEventId = 99L;
        Long categoryId = 1L;
        CreateReviewDTO createDTO = new CreateReviewDTO(nonExistentEventId, categoryId, 5, "This should fail.");

        when(eventRepository.findById(nonExistentEventId)).thenReturn(Optional.empty());

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            reviewService.createReview(createDTO, userId);
        }, "Should throw EventNotFoundException.");

        assertEquals("Event not found", exception.getMessage());

        verify(userRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw exception when user does not exist")
    void createReview_shouldThrowException_whenUserNotFound() {
        // Given
        Long nonExistentUserId = 99L;
        Long eventId = 1L;
        Long categoryId = 1L;
        CreateReviewDTO createDTO = new CreateReviewDTO(eventId, categoryId, 5, "This should also fail.");
        Event event = Event.builder().id(eventId).name("Some Event").build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            reviewService.createReview(createDTO, nonExistentUserId);
        }, "Should throw UserNotFoundException.");

        assertEquals("User not found", exception.getMessage());

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Should create review successfully with null comment")
    void createReview_shouldSucceed_whenCommentIsNull() {
        // Given
        Long userId = 1L;
        Long eventId = 1L;
        Long categoryId = 1L;
        LocalDateTime now = LocalDateTime.now();
        CreateReviewDTO createDTO = new CreateReviewDTO(eventId, categoryId, 4, null);
        User user = User.builder().id(userId).firstName("Anna").lastName("Nowak").build();
        Event event = Event.builder().id(eventId).name("Festival").build();
        Review reviewToSave = Review.builder().user(user).event(event).rating(4).comment(null).build();
        Review savedReview = Review.builder().id(2L).user(user).event(event).rating(4).comment(null).createdAt(now).build();
        ReviewDTO expectedDTO = new ReviewDTO(2L, eventId, "Festival", userId, "Anna Nowak", 4, null, now);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reviewMapper.toEntity(createDTO, event, user)).thenReturn(reviewToSave);
        when(reviewRepository.save(reviewToSave)).thenReturn(savedReview);
        when(reviewMapper.toDTO(savedReview)).thenReturn(expectedDTO);

        // When
        ReviewDTO result = reviewService.createReview(createDTO, userId);

        // Then
        assertNotNull(result, "Returned DTO should not be null.");
        assertNull(result.comment(), "Comment in the returned DTO should be null.");
        assertEquals(expectedDTO.id(), result.id());
        assertEquals(expectedDTO.rating(), result.rating());

        verify(reviewRepository).save(reviewToSave);
    }
}