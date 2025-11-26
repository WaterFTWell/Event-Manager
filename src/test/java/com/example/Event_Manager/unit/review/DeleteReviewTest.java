package com.example.Event_Manager.unit.review;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.review.Review;
import com.example.Event_Manager.models.review.exceptions.ReviewNotFoundException;
import com.example.Event_Manager.models.review.repository.ReviewRepository;
import com.example.Event_Manager.models.review.service.ReviewService;
import com.example.Event_Manager.models.review.validation.ReviewValidation;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.models.user.validation.UserValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Review Deletion")
public class DeleteReviewTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewValidation reviewValidation;

    @Mock
    private UserValidation userValidation;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("Should delete review successfully when valid IDs are provided")
    void deleteReview_shouldSucceed_whenIdsAreValid() {
        // Given
        Long reviewId = 1L;
        Long userId = 10L;

        Review review = new Review();
        review.setId(reviewId);

        User user = new User();
        user.setId(userId);

        doNothing().when(reviewValidation).checkIfRequestNotNull(reviewId);
        doNothing().when(reviewValidation).checkIfIdValid(reviewId);
        doNothing().when(userValidation).checkIfIdValid(userId);

        when(reviewRepository.getReviewById(reviewId)).thenReturn(Optional.of(review));
        doNothing().when(reviewValidation).checkIfObjectExist(review);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userValidation).checkIfObjectExist(user);

        doNothing().when(reviewRepository).deleteById(reviewId);

        // When
        reviewService.deleteReview(reviewId, userId);

        // Then
        verify(reviewValidation).checkIfRequestNotNull(reviewId);
        verify(reviewValidation).checkIfIdValid(reviewId);
        verify(userValidation).checkIfIdValid(userId);
        verify(reviewRepository).getReviewById(reviewId);
        verify(reviewValidation).checkIfObjectExist(review);
        verify(userRepository).findById(userId);
        verify(userValidation).checkIfObjectExist(user);
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    @DisplayName("Should throw exception when reviewId is null")
    void deleteReview_shouldThrowException_whenReviewIdIsNull() {
        // Given
        Long reviewId = null;
        Long userId = 10L;

        doThrow(new ReviewNotFoundException("Request cannot be null."))
                .when(reviewValidation).checkIfRequestNotNull(reviewId);

        // When & Then
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.deleteReview(reviewId, userId);
        }, "Should throw ReviewNotFoundException for null reviewId.");

        assertEquals("Request cannot be null.", exception.getMessage());

        verify(reviewValidation).checkIfRequestNotNull(reviewId);
        verifyNoMoreInteractions(reviewValidation);
        verifyNoInteractions(userValidation, reviewRepository, userRepository);
    }

    @Test
    @DisplayName("Should throw exception when reviewId is invalid (e.g., zero or negative)")
    void deleteReview_shouldThrowException_whenReviewIdIsInvalid() {
        // Given
        Long invalidReviewId = 0L;
        Long userId = 10L;

        doNothing().when(reviewValidation).checkIfRequestNotNull(invalidReviewId);
        doThrow(new ReviewNotFoundException("ID must be greater than 0."))
                .when(reviewValidation).checkIfIdValid(invalidReviewId);

        // When & Then
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.deleteReview(invalidReviewId, userId);
        });

        assertEquals("ID must be greater than 0.", exception.getMessage());

        verify(reviewValidation).checkIfRequestNotNull(invalidReviewId);
        verify(reviewValidation).checkIfIdValid(invalidReviewId);
        verify(reviewRepository, never()).getReviewById(anyLong());
        verify(reviewRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when userId is invalid")
    void deleteReview_shouldThrowException_whenUserIdIsInvalid() {
        // Given
        Long reviewId = 1L;
        Long invalidUserId = -5L;

        doNothing().when(reviewValidation).checkIfRequestNotNull(reviewId);
        doNothing().when(reviewValidation).checkIfIdValid(reviewId);
        doThrow(new UserNotFoundException("ID must be greater than 0."))
                .when(userValidation).checkIfIdValid(invalidUserId);

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            reviewService.deleteReview(reviewId, invalidUserId);
        });

        assertEquals("ID must be greater than 0.", exception.getMessage());

        verify(reviewValidation).checkIfRequestNotNull(reviewId);
        verify(reviewValidation).checkIfIdValid(reviewId);
        verify(userValidation).checkIfIdValid(invalidUserId);
        verify(reviewRepository, never()).getReviewById(anyLong());
        verify(reviewRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when review is not found")
    void deleteReview_shouldThrowException_whenReviewNotFound() {
        // Given
        Long reviewId = 999L;
        Long userId = 10L;

        doNothing().when(reviewValidation).checkIfRequestNotNull(reviewId);
        doNothing().when(reviewValidation).checkIfIdValid(reviewId);
        doNothing().when(userValidation).checkIfIdValid(userId);

        when(reviewRepository.getReviewById(reviewId)).thenReturn(Optional.empty());

        // When & Then
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.deleteReview(reviewId, userId);
        });

        assertEquals("Review not found", exception.getMessage());

        verify(reviewRepository).getReviewById(reviewId);
        verify(reviewRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void deleteReview_shouldThrowException_whenUserNotFound() {
        // Given
        Long reviewId = 1L;
        Long userId = 999L;

        Review review = new Review();
        review.setId(reviewId);

        doNothing().when(reviewValidation).checkIfRequestNotNull(reviewId);
        doNothing().when(reviewValidation).checkIfIdValid(reviewId);
        doNothing().when(userValidation).checkIfIdValid(userId);

        when(reviewRepository.getReviewById(reviewId)).thenReturn(Optional.of(review));
        doNothing().when(reviewValidation).checkIfObjectExist(review);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            reviewService.deleteReview(reviewId, userId);
        });

        assertEquals("User not found", exception.getMessage());

        verify(reviewRepository).getReviewById(reviewId);
        verify(userRepository).findById(userId);
        verify(reviewRepository, never()).deleteById(anyLong());
    }
}