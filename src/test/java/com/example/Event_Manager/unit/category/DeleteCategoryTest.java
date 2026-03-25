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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Category Deletion")
public class DeleteCategoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Should delete category successfully when it exists")
    void deleteCategory_shouldSucceed_whenCategoryExists() {
        Long categoryId = 1L;
        when(categoryRepository.deleteCategoryById(categoryId)).thenReturn(1);

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).deleteCategoryById(categoryId);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when trying to delete a non-existent category")
    void deleteCategory_shouldThrowException_whenCategoryDoesNotExist() {
        Long nonExistentId = 99L;
        when(categoryRepository.deleteCategoryById(nonExistentId)).thenReturn(0);

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(nonExistentId));
    }
}