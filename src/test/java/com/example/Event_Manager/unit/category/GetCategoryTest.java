package com.example.Event_Manager.unit.category;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.dto.response.CategoryDTO;
import com.example.Event_Manager.category.exceptions.CategoriesNotFoundException;
import com.example.Event_Manager.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.category.mapper.CategoryMapper;
import com.example.Event_Manager.category.repository.CategoryRepository;
import com.example.Event_Manager.category.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
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

//    @Mock
//    private CategoryValidation categoryValidation;

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
        when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category1));
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
        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(nonExistentId));

        assertEquals("Category not found in database.", exception.getMessage());
        verify(categoryRepository).findById(nonExistentId);
        verify(categoryMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("getAllCategories: Should return a page of categories when they exist")
    void getAllCategories_shouldReturnCategoryPage_whenCategoriesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDTO(category1)).thenReturn(categoryDTO1);
        when(categoryMapper.toDTO(category2)).thenReturn(categoryDTO2);

        // When
        Page<CategoryDTO> result = categoryService.getAllCategories(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertThat(result.getContent()).containsExactlyInAnyOrder(categoryDTO1, categoryDTO2);
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getTotalPages());

        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper, times(2)).toDTO(any(Category.class));
    }

    @Test
    @DisplayName("getAllCategories: Should return correct page when requesting second page")
    void getAllCategories_shouldReturnSecondPage_whenPageableIsSecondPage() {
        // Given
        Pageable pageable = PageRequest.of(1, 5);
        Category category3 = Category.builder().id(3L).name("Teatr").description("Wydarzenia teatralne").build();
        CategoryDTO categoryDTO3 = new CategoryDTO(3L, "Teatr", "Wydarzenia teatralne");

        List<Category> categories = List.of(category3);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, 6);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDTO(category3)).thenReturn(categoryDTO3);

        // When
        Page<CategoryDTO> result = categoryService.getAllCategories(pageable);

        // Then
        assertNotNull(result);
        assertEquals(6, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getNumber());
        assertEquals(5, result.getSize());
        assertEquals(2, result.getTotalPages());
        assertTrue(result.hasPrevious());
        assertFalse(result.hasNext());

        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDTO(category3);
    }

    @Test
    @DisplayName("getAllCategories: Should return a page with one category")
    void getAllCategories_shouldReturnSingleCategoryPage_whenOneCategoryExists() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category1);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, 1);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDTO(category1)).thenReturn(categoryDTO1);

        // When
        Page<CategoryDTO> result = categoryService.getAllCategories(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(categoryDTO1, result.getContent().getFirst());
        assertEquals(1, result.getTotalPages());
        assertFalse(result.hasNext());
        assertFalse(result.hasPrevious());

        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDTO(category1);
    }

    @Test
    @DisplayName("getAllCategories: Should throw CategoryNotFoundException when no categories are in the database")
    void getAllCategories_shouldThrowException_whenNoCategoriesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(categoryRepository.findAll(pageable)).thenReturn(emptyPage);

        // When & Then
        CategoriesNotFoundException exception = assertThrows(CategoriesNotFoundException.class, () -> categoryService.getAllCategories(pageable));

        assertEquals("No categories found in database.", exception.getMessage());
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("getAllCategories: Should handle custom page size correctly")
    void getAllCategories_shouldHandleCustomPageSize() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Category category3 = Category.builder().id(3L).name("Teatr").description("Wydarzenia teatralne").build();
        CategoryDTO categoryDTO3 = new CategoryDTO(3L, "Teatr", "Wydarzenia teatralne");

        List<Category> categories = List.of(category1, category2, category3);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, 10);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDTO(category1)).thenReturn(categoryDTO1);
        when(categoryMapper.toDTO(category2)).thenReturn(categoryDTO2);
        when(categoryMapper.toDTO(category3)).thenReturn(categoryDTO3);

        // When
        Page<CategoryDTO> result = categoryService.getAllCategories(pageable);

        // Then
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(3, result.getContent().size());
        assertEquals(0, result.getNumber());
        assertEquals(5, result.getSize());
        assertEquals(2, result.getTotalPages());
        assertTrue(result.hasNext());

        verify(categoryRepository).findAll(pageable);
    }

    @Test
    @DisplayName("getAllCategories: Should map all categories correctly")
    void getAllCategories_shouldMapAllCategoriesCorrectly() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, 2);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDTO(category1)).thenReturn(categoryDTO1);
        when(categoryMapper.toDTO(category2)).thenReturn(categoryDTO2);

        // When
        Page<CategoryDTO> result = categoryService.getAllCategories(pageable);

        // Then
        List<CategoryDTO> content = result.getContent();
        assertTrue(content.contains(categoryDTO1));
        assertTrue(content.contains(categoryDTO2));
        assertEquals("Koncerty", content.stream()
                .filter(dto -> dto.id().equals(1L))
                .findFirst()
                .get()
                .name());
        assertEquals("Sport", content.stream()
                .filter(dto -> dto.id().equals(2L))
                .findFirst()
                .get()
                .name());
    }
}