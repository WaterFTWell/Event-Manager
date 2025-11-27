package com.example.Event_Manager.unit.review;

import com.example.Event_Manager.user.repository.UserRepository;
import com.example.Event_Manager.event.Event;
import com.example.Event_Manager.review.Review;
import com.example.Event_Manager.review.dto.request.UpdateReviewDTO;
import com.example.Event_Manager.review.dto.response.ReviewDTO;
import com.example.Event_Manager.review.exceptions.ReviewNotFoundException;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Review Update")
public class UpdateReviewTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private ReviewValidation reviewValidation;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("Should update review successfully with valid data")
    void updateReview_shouldSucceed_whenDataIsValid() {
        // Given
        Long reviewId = 1L;
        Long userId = 10L;
        Long categoryId = 5L;
        UpdateReviewDTO updateDTO = new UpdateReviewDTO(categoryId, 9, "Updated comment!");

        User user = User.builder().id(userId).firstName("Jan").lastName("Kowalski").build();
        Event event = Event.builder().id(1L).name("Existing Event").build();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        Review existingReview = Review.builder()
                .id(reviewId)
                .user(user)
                .event(event)
                .rating(5)
                .comment("Original comment")
                .createdAt(createdAt)
                .build();

        Review updatedReview = Review.builder()
                .id(reviewId)
                .user(user)
                .event(event)
                .rating(9)
                .comment("Updated comment!")
                .createdAt(createdAt)
                .updatedAt(LocalDateTime.now())
                .build();

        ReviewDTO expectedDTO = new ReviewDTO(reviewId, event.getId(), event.getName(),
                userId, user.getFullName(), 9, "Updated comment!", createdAt);

        // doNothing().when(reviewValidation).checkIfRequestNotNull(updateDTO); // USUNIĘTE
        // doNothing().when(reviewValidation).checkIfIdValid(reviewId); // USUNIĘTE
        when(reviewRepository.getReviewById(reviewId)).thenReturn(Optional.of(existingReview));
        doNothing().when(reviewMapper).updateEntity(existingReview, updateDTO);
        when(reviewRepository.save(existingReview)).thenReturn(updatedReview);
        when(reviewMapper.toDTO(updatedReview)).thenReturn(expectedDTO);

        // When
        ReviewDTO result = reviewService.updateReview(reviewId, updateDTO, user);

        // Then
        assertNotNull(result);
        assertEquals(expectedDTO.id(), result.id());
        assertEquals(expectedDTO.rating(), result.rating());
        assertEquals(expectedDTO.comment(), result.comment());

        verify(reviewRepository).getReviewById(reviewId);
        verify(reviewMapper).updateEntity(existingReview, updateDTO);
        verify(reviewRepository).save(existingReview);
        verify(reviewMapper).toDTO(updatedReview);
    }

    @Test
    @DisplayName("Should throw ReviewNotFoundException when review does not exist")
    void updateReview_shouldThrowException_whenReviewNotFound() {
        // Given
        Long nonExistentReviewId = 99L;
        Long userId = 10L;
        UpdateReviewDTO updateDTO = new UpdateReviewDTO(1L, 5, "some comment");
        User user = User.builder().id(userId).firstName("Jan").lastName("Kowalski").build();

        when(reviewRepository.getReviewById(nonExistentReviewId)).thenReturn(Optional.empty());

        // When & Then
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.updateReview(nonExistentReviewId, updateDTO, user);
        });

        assertEquals("Review not found", exception.getMessage());

        verify(reviewRepository).getReviewById(nonExistentReviewId);
        verify(reviewRepository, never()).save(any(Review.class));
        verify(reviewMapper, never()).updateEntity(any(Review.class), any(UpdateReviewDTO.class));
    }


    @Test
    @DisplayName("Should update only rating when comment is null")
    void updateReview_shouldSucceed_whenOnlyRatingIsUpdated() {
        // Given
        Long reviewId = 1L;
        Long userId = 10L;
        UpdateReviewDTO updateDTO = new UpdateReviewDTO(null, 10, null);

        User user = User.builder().id(userId).firstName("Anna").lastName("Nowak").build();
        Event event = Event.builder().id(1L).name("Event").build();

        Review existingReview = Review.builder()
                .id(reviewId)
                .user(user)
                .event(event)
                .rating(5)
                .comment("Original comment")
                .build();

        Review updatedReview = Review.builder()
                .id(reviewId)
                .user(user)
                .event(event)
                .rating(10)
                .comment("Original comment")
                .build();

        ReviewDTO expectedDTO = new ReviewDTO(reviewId, event.getId(), event.getName(),
                userId, user.getFullName(), 10, "Original comment", null);

        when(reviewRepository.getReviewById(reviewId)).thenReturn(Optional.of(existingReview));
        doNothing().when(reviewMapper).updateEntity(existingReview, updateDTO);
        when(reviewRepository.save(existingReview)).thenReturn(updatedReview);
        when(reviewMapper.toDTO(updatedReview)).thenReturn(expectedDTO);

        // When
        ReviewDTO result = reviewService.updateReview(reviewId, updateDTO, user);

        // Then
        assertNotNull(result);
        assertEquals(10, result.rating());
        assertEquals("Original comment", result.comment());

        verify(reviewMapper).updateEntity(existingReview, updateDTO);
        verify(reviewRepository).save(existingReview);
    }
}