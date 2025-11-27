package com.example.Event_Manager.unit.category;

import com.example.Event_Manager.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.category.repository.CategoryRepository;
import com.example.Event_Manager.category.service.CategoryService;
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

//    @Mock
//    private CategoryValidation categoryValidation;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Should delete category successfully when it exists")
    void deleteCategory_shouldSucceed_whenCategoryExists() {
        // Given
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(categoryId);

        // When
        categoryService.deleteCategory(categoryId);

        // Then
        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when trying to delete a non-existent category")
    void deleteCategory_shouldThrowException_whenCategoryDoesNotExist() {
        // Given
        Long nonExistentId = 99L;
        when(categoryRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.deleteCategory(nonExistentId);
        });

        assertEquals("Category with this id is not in database.", exception.getMessage());

        verify(categoryRepository).existsById(nonExistentId);
        verify(categoryRepository, never()).deleteById(nonExistentId);
    }
}