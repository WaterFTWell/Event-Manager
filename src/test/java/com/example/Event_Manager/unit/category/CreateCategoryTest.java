package com.example.Event_Manager.unit.category;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.category.dto.response.CategoryDTO;
import com.example.Event_Manager.category.exceptions.CategoryAlreadyExistsException;
import com.example.Event_Manager.category.mapper.CategoryMapper;
import com.example.Event_Manager.category.repository.CategoryRepository;
import com.example.Event_Manager.category.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Category Creation")
public class CreateCategoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Should create category successfully with valid data")
    void createCategory_shouldSucceed_whenDataIsValid() {
        // Given
        CreateCategoryDTO createDTO = new CreateCategoryDTO("Koncerty", "Wydarzenia muzyczne na żywo.");
        Category categoryToSave = Category.builder().name(createDTO.name()).description(createDTO.description()).build();
        Category savedCategory = Category.builder().id(1L).name(createDTO.name()).description(createDTO.description()).build();
        CategoryDTO expectedDTO = new CategoryDTO(1L, createDTO.name(), createDTO.description());

        when(categoryRepository.findCategoryByName(createDTO.name())).thenReturn(Optional.empty());
        when(categoryMapper.toEntity(createDTO)).thenReturn(categoryToSave);
        when(categoryRepository.save(categoryToSave)).thenReturn(savedCategory);
        when(categoryMapper.toDTO(savedCategory)).thenReturn(expectedDTO);

        // When
        CategoryDTO result = categoryService.createCategory(createDTO);

        // Then
        assertNotNull(result, "Zwrócone DTO nie powinno być nullem.");
        assertEquals(expectedDTO.id(), result.id());
        assertEquals(expectedDTO.name(), result.name());

        // Weryfikacja interakcji
        verify(categoryRepository).findCategoryByName(createDTO.name());
        verify(categoryRepository).save(categoryToSave);
        verify(categoryMapper).toDTO(savedCategory);
    }

    @Test
    @DisplayName("Should throw exception when category name already exists")
    void createCategory_shouldThrowException_whenNameIsDuplicate() {
        // Given
        CreateCategoryDTO createDTO = new CreateCategoryDTO("Sport", "Wydarzenia sportowe.");
        Category existingCategory = Category.builder().id(1L).name(createDTO.name()).description(createDTO.description()).build();

        when(categoryRepository.findCategoryByName(createDTO.name())).thenReturn(Optional.ofNullable(existingCategory));

        // When & Then
        CategoryAlreadyExistsException exception = assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createCategory(createDTO), "Powinien zostać rzucony wyjątek o istniejącej nazwie kategorii.");

        assertEquals("Category with this name already exists.", exception.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryRepository).findCategoryByName(createDTO.name());
    }

    @Test
    @DisplayName("Should create category successfully with edge case description length")
    void createCategory_shouldSucceed_withLongDescription() {
        // Given
        String longDescription = "a".repeat(500);
        CreateCategoryDTO createDTO = new CreateCategoryDTO("Długi Opis", longDescription);
        Category categoryToSave = Category.builder().name(createDTO.name()).description(createDTO.description()).build();
        Category savedCategory = Category.builder().id(1L).name("Długi Opis").description(longDescription).build();
        CategoryDTO expectedDTO = new CategoryDTO(1L, "Długi Opis", longDescription);

        when(categoryRepository.findCategoryByName(createDTO.name())).thenReturn(Optional.empty());
        when(categoryMapper.toEntity(createDTO)).thenReturn(categoryToSave);
        when(categoryRepository.save(categoryToSave)).thenReturn(savedCategory);
        when(categoryMapper.toDTO(savedCategory)).thenReturn(expectedDTO);

        // When
        CategoryDTO result = categoryService.createCategory(createDTO);

        // Then
        assertNotNull(result);
        assertEquals(longDescription, result.description());
        verify(categoryRepository).save(any(Category.class));
    }
}