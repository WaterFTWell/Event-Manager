package com.example.Event_Manager.unit.category;

import com.example.Event_Manager.models.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.category.service.CategoryService;
import com.example.Event_Manager.models.category.validation.CategoryValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Category Deletion")
public class DeleteCategoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryValidation categoryValidation;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Should delete category successfully when it exists")
    void deleteCategory_shouldSucceed_whenCategoryExists() {
        // Given
        Long categoryId = 1L;
        doNothing().when(categoryValidation).checkIfIdValid(categoryId);
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(categoryId);

        // When
        categoryService.deleteCategory(categoryId);

        // Then
        verify(categoryValidation, times(1)).checkIfIdValid(categoryId);
        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when trying to delete a non-existent category")
    void deleteCategory_shouldThrowException_whenCategoryDoesNotExist() {
        // Given
        Long nonExistentId = 99L;
        doNothing().when(categoryValidation).checkIfIdValid(nonExistentId);
        when(categoryRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.deleteCategory(nonExistentId);
        }, "Powinien zostać rzucony wyjątek CategoryNotFoundException.");

        assertEquals("Category with this id is not in database.", exception.getMessage());

        verify(categoryRepository, never()).deleteById(nonExistentId);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for a null ID")
    void deleteCategory_shouldThrowException_whenIdIsNull() {
        // Given
        Long nullId = null;
        doThrow(new IllegalArgumentException("ID cannot be null."))
                .when(categoryValidation).checkIfIdValid(nullId);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.deleteCategory(nullId);
        });

        assertEquals("ID cannot be null.", exception.getMessage());

        verify(categoryRepository, never()).existsById(any());
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for a non-positive ID (e.g., zero)")
    void deleteCategory_shouldThrowException_whenIdIsZero() {
        // Given
        Long zeroId = 0L;
        doThrow(new IllegalArgumentException("ID must be positive."))
                .when(categoryValidation).checkIfIdValid(zeroId);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.deleteCategory(zeroId);
        });

        assertEquals("ID must be positive.", exception.getMessage());

        verify(categoryRepository, never()).existsById(any());
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for a negative ID")
    void deleteCategory_shouldThrowException_whenIdIsNegative() {
        // Given
        Long negativeId = -5L;
        doThrow(new IllegalArgumentException("ID must be positive."))
                .when(categoryValidation).checkIfIdValid(negativeId);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.deleteCategory(negativeId);
        });

        assertEquals("ID must be positive.", exception.getMessage());

        verify(categoryRepository, never()).existsById(any());
        verify(categoryRepository, never()).deleteById(any());
    }
}