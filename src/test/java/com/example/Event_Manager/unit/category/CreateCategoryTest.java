package com.example.Event_Manager.unit.category;

import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;
import com.example.Event_Manager.models.category.exceptions.CategoryAlreadyExistsException;
import com.example.Event_Manager.models.category.mapper.CategoryMapper;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.category.service.CategoryService;
import com.example.Event_Manager.models.category.validation.CategoryValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Category Creation")
public class CreateCategoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryValidation categoryValidation;

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

        // Symulacja działania mocków
        doNothing().when(categoryValidation).checkIfRequestNotNull(createDTO);
        doNothing().when(categoryValidation).checkIfNameUnique(createDTO.name());
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
        verify(categoryValidation).checkIfRequestNotNull(createDTO);
        verify(categoryValidation).checkIfNameUnique(createDTO.name());
        verify(categoryRepository).save(categoryToSave);
        verify(categoryMapper).toDTO(savedCategory);
    }

    @Test
    @DisplayName("Should throw exception when category name already exists")
    void createCategory_shouldThrowException_whenNameIsDuplicate() {
        // Given
        CreateCategoryDTO createDTO = new CreateCategoryDTO("Sport", "Wydarzenia sportowe.");
        doNothing().when(categoryValidation).checkIfRequestNotNull(createDTO);
        doThrow(new CategoryAlreadyExistsException("Category with this name already exists."))
                .when(categoryValidation).checkIfNameUnique(createDTO.name());

        // When & Then
        CategoryAlreadyExistsException exception = assertThrows(CategoryAlreadyExistsException.class, () -> {
            categoryService.createCategory(createDTO);
        }, "Powinien zostać rzucony wyjątek o istniejącej nazwie kategorii.");

        assertEquals("Category with this name already exists.", exception.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception when request DTO is null")
    void createCategory_shouldThrowException_whenDtoIsNull() {
        // Given
        CreateCategoryDTO nullDto = null;
        doThrow(new IllegalArgumentException("Request cannot be null."))
                .when(categoryValidation).checkIfRequestNotNull(nullDto);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(nullDto);
        });

        assertEquals("Request cannot be null.", exception.getMessage());

        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("Should throw exception for blank category name")
    void createCategory_shouldThrowException_whenNameIsBlank() {
        // Given
        CreateCategoryDTO createDTO = new CreateCategoryDTO("   ", "Opis z pustą nazwą.");
        doNothing().when(categoryValidation).checkIfRequestNotNull(createDTO);
        doThrow(new IllegalArgumentException("Category name cannot be blank."))
                .when(categoryValidation).checkIfNameUnique(createDTO.name());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(createDTO);
        });

        assertEquals("Category name cannot be blank.", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create category successfully with edge case description length")
    void createCategory_shouldSucceed_withLongDescription() {
        // Given
        String longDescription = "a".repeat(500);
        CreateCategoryDTO createDTO = new CreateCategoryDTO("Długi Opis", longDescription);
        Category category = Category.builder().id(1L).name("Długi Opis").description(longDescription).build();
        CategoryDTO expectedDTO = new CategoryDTO(1L, "Długi Opis", longDescription);

        when(categoryMapper.toEntity(any())).thenReturn(category);
        when(categoryRepository.save(any())).thenReturn(category);
        when(categoryMapper.toDTO(any())).thenReturn(expectedDTO);

        // When
        CategoryDTO result = categoryService.createCategory(createDTO);

        // Then
        assertNotNull(result);
        assertEquals(longDescription, result.description());
        verify(categoryRepository).save(any(Category.class));
    }
}
