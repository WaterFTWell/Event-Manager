package com.example.Event_Manager.unit.review;

import com.example.Event_Manager.models._util.RequestEmptyException;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.review.Review;
import com.example.Event_Manager.models.review.dto.request.UpdateReviewDTO;
import com.example.Event_Manager.models.review.dto.response.ReviewDTO;
import com.example.Event_Manager.models.review.exceptions.ReviewNotFoundException;
import com.example.Event_Manager.models.review.mapper.ReviewMapper;
import com.example.Event_Manager.models.review.repository.ReviewRepository;
import com.example.Event_Manager.models.review.service.ReviewService;
import com.example.Event_Manager.models.review.validation.ReviewValidation;
import com.example.Event_Manager.models.user.User;
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

        ReviewDTO expectedDTO = new ReviewDTO(reviewId, event.getId(), event.getName(), userId, user.getFullName(), 9, "Updated comment!", createdAt);

        doNothing().when(reviewValidation).checkIfRequestNotNull(updateDTO);
        doNothing().when(reviewValidation).checkIfIdValid(reviewId);
        when(reviewRepository.getReviewById(reviewId)).thenReturn(Optional.of(existingReview));
        doNothing().when(reviewValidation).checkIfObjectExist(existingReview);
        doNothing().when(reviewMapper).updateEntity(existingReview, updateDTO);
        when(reviewRepository.save(existingReview)).thenReturn(updatedReview);
        when(reviewMapper.toDTO(updatedReview)).thenReturn(expectedDTO);

        // When
        ReviewDTO result = reviewService.updateReview(reviewId, updateDTO, userId);

        // Then
        assertNotNull(result);
        assertEquals(expectedDTO.id(), result.id());
        assertEquals(expectedDTO.rating(), result.rating());
        assertEquals(expectedDTO.comment(), result.comment());

        verify(reviewValidation).checkIfRequestNotNull(updateDTO);
        verify(reviewValidation).checkIfIdValid(reviewId);
        verify(reviewRepository).getReviewById(reviewId);
        verify(reviewValidation).checkIfObjectExist(existingReview);
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

        doNothing().when(reviewValidation).checkIfRequestNotNull(updateDTO);
        doNothing().when(reviewValidation).checkIfIdValid(nonExistentReviewId);
        when(reviewRepository.getReviewById(nonExistentReviewId)).thenReturn(Optional.empty());

        // When & Then
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.updateReview(nonExistentReviewId, updateDTO, userId);
        });

        assertEquals("Review not found", exception.getMessage());

        verify(reviewRepository, never()).save(any(Review.class));
        verify(reviewMapper, never()).updateEntity(any(Review.class), any(UpdateReviewDTO.class));
    }

    @Test
    @DisplayName("Should throw exception when update DTO is null")
    void updateReview_shouldThrowException_whenDtoIsNull() {
        // Given
        Long reviewId = 1L;
        Long userId = 10L;
        UpdateReviewDTO nullDto = null;

        doThrow(new RequestEmptyException("Request cannot be null."))
                .when(reviewValidation).checkIfRequestNotNull(nullDto);

        // When & Then
        RequestEmptyException exception = assertThrows(RequestEmptyException.class, () -> {
            reviewService.updateReview(reviewId, nullDto, userId);
        });

        assertEquals("Request cannot be null.", exception.getMessage());

        verify(reviewRepository, never()).getReviewById(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid review ID")
    void updateReview_shouldThrowException_whenReviewIdIsInvalid() {
        // Given
        Long invalidReviewId = -1L;
        Long userId = 10L;
        UpdateReviewDTO updateDTO = new UpdateReviewDTO(1L, 5, "some comment");

        doNothing().when(reviewValidation).checkIfRequestNotNull(updateDTO);
        doThrow(new ReviewNotFoundException("ID must be greater than 0."))
                .when(reviewValidation).checkIfIdValid(invalidReviewId);

        // When & Then
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.updateReview(invalidReviewId, updateDTO, userId);
        });

        assertEquals("ID must be greater than 0.", exception.getMessage());

        verify(reviewRepository, never()).getReviewById(anyLong());
    }
}