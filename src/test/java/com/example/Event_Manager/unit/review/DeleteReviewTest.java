package com.example.Event_Manager.unit.review;

import com.example.Event_Manager.user.repository.UserRepository;
import com.example.Event_Manager.review.Review;
import com.example.Event_Manager.review.exceptions.ReviewNotFoundException;
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


        when(reviewRepository.getReviewById(reviewId)).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).deleteById(reviewId);

        // When
        reviewService.deleteReview(reviewId, user);

        // Then
        verify(reviewRepository).getReviewById(reviewId);
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    @DisplayName("Should throw exception when review is not found")
    void deleteReview_shouldThrowException_whenReviewNotFound() {
        // Given
        Long reviewId = 999L;
        User user = new User();

        when(reviewRepository.getReviewById(reviewId)).thenReturn(Optional.empty());

        // When & Then
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.deleteReview(reviewId, user);
        });

        assertEquals("Review not found", exception.getMessage());

        verify(reviewRepository).getReviewById(reviewId);
        verify(reviewRepository, never()).deleteById(anyLong());
    }
}