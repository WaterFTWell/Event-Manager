package com.example.Event_Manager.unit.category;

import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;
import com.example.Event_Manager.models.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.models.category.exceptions.InvalidCategoryException;
import com.example.Event_Manager.models.category.mapper.CategoryMapper;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.category.service.CategoryService;
import com.example.Event_Manager.models.category.validation.CategoryValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Getting Categories")
public class GetCategoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryValidation categoryValidation;

    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private CategoryDTO categoryDTO1;
    private Category category2;
    private CategoryDTO categoryDTO2;

    @BeforeEach
    void setUp() {
        category1 = Category.builder().id(1L).name("Koncerty").description("Wydarzenia muzyczne").build();
        categoryDTO1 = new CategoryDTO(1L, "Koncerty", "Wydarzenia muzyczne");

        category2 = Category.builder().id(2L).name("Sport").description("Wydarzenia sportowe").build();
        categoryDTO2 = new CategoryDTO(2L, "Sport", "Wydarzenia sportowe");
    }

    @Test
    @DisplayName("getCategoryById: Should return category when ID exists")
    void getCategoryById_shouldReturnCategory_whenIdExists() {
        // Given
        Long existingId = 1L;
        doNothing().when(categoryValidation).checkIfIdValid(existingId);
        when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category1));
        doNothing().when(categoryValidation).checkIfObjectExist(category1);
        when(categoryMapper.toDTO(category1)).thenReturn(categoryDTO1);

        // When
        CategoryDTO result = categoryService.getCategoryById(existingId);

        // Then
        assertNotNull(result);
        assertEquals(categoryDTO1, result);
        verify(categoryRepository).findById(existingId);
        verify(categoryMapper).toDTO(category1);
    }

    @Test
    @DisplayName("getCategoryById: Should throw CategoryNotFoundException when ID does not exist")
    void getCategoryById_shouldThrowException_whenIdDoesNotExist() {
        // Given
        Long nonExistentId = 99L;
        doNothing().when(categoryValidation).checkIfIdValid(nonExistentId);
        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.getCategoryById(nonExistentId);
        });

        assertEquals("Category not found in database.", exception.getMessage());
        verify(categoryMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("getCategoryById: Should throw InvalidCategoryException for non-positive ID")
    void getCategoryById_shouldThrowException_whenIdIsNotPositive() {
        // Given
        Long invalidId = 0L;
        doThrow(new InvalidCategoryException("ID must be positive.")).when(categoryValidation).checkIfIdValid(invalidId);

        // When & Then
        assertThrows(InvalidCategoryException.class, () -> {
            categoryService.getCategoryById(invalidId);
        });

        verify(categoryRepository, never()).findById(any());
    }

    @Test
    @DisplayName("getAllCategories: Should return a list of categories when they exist")
    void getAllCategories_shouldReturnCategoryList_whenCategoriesExist() {
        // Given
        List<Category> categories = List.of(category1, category2);
        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDTO(category1)).thenReturn(categoryDTO1);
        when(categoryMapper.toDTO(category2)).thenReturn(categoryDTO2);

        // When
        List<CategoryDTO> result = categoryService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertThat(result).containsExactlyInAnyOrder(categoryDTO1, categoryDTO2);
        verify(categoryRepository).findAll();
        verify(categoryMapper, times(2)).toDTO(any(Category.class));
    }

    @Test
    @DisplayName("getAllCategories: Should return a list with one category")
    void getAllCategories_shouldReturnSingleCategoryList_whenOneCategoryExists() {
        // Given
        List<Category> categories = List.of(category1);
        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDTO(category1)).thenReturn(categoryDTO1);

        // When
        List<CategoryDTO> result = categoryService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(categoryDTO1, result.get(0));
    }

    @Test
    @DisplayName("getAllCategories: Should throw CategoryNotFoundException when no categories are in the database")
    void getAllCategories_shouldThrowException_whenNoCategoriesExist() {
        // Given
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.getAllCategories();
        });

        assertEquals("No categories found in database.", exception.getMessage());
        verify(categoryMapper, never()).toDTO(any());
    }
}