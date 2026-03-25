package com.example.Event_Manager.unit.category;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.category.dto.response.CategoryDTO;
import com.example.Event_Manager.category.exceptions.CategoryAlreadyExistsException;
import com.example.Event_Manager.category.mapper.CategoryMapper;
import com.example.Event_Manager.category.repository.CategoryRepository;
import com.example.Event_Manager.category.service.CategoryService;
import com.example.Event_Manager.category.service.validation.ICategoryValidation;
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
    private ICategoryValidation categoryValidation;

    @InjectMocks
    private CategoryService categoryService;


    @Test
    @DisplayName("Should create category successfully with valid data")
    void createCategory_shouldSucceed_whenDataIsValid() {
        CreateCategoryDTO createDTO = new CreateCategoryDTO("Koncerty", "Wydarzenia muzyczne na żywo.");
        String trimmedName = "Koncerty";

        Category categoryToSave = Category.builder().name(trimmedName).description(createDTO.description()).build();
        Category savedCategory = Category.builder().id(1L).name(trimmedName).description(createDTO.description()).build();
        CategoryDTO expectedDTO = new CategoryDTO(1L, trimmedName, createDTO.description());

        when(categoryValidation.validateAndTrimName(createDTO.name())).thenReturn(trimmedName);
        doNothing().when(categoryValidation).checkForDuplicateCategoryName(trimmedName, null);

        when(categoryMapper.toEntityWithTrimmedName(createDTO, trimmedName)).thenReturn(categoryToSave);
        when(categoryRepository.save(categoryToSave)).thenReturn(savedCategory);
        when(categoryMapper.toDTO(savedCategory)).thenReturn(expectedDTO);

        CategoryDTO result = categoryService.createCategory(createDTO);

        assertNotNull(result, "Zwrócone DTO nie powinno być nullem.");
        assertEquals(expectedDTO.id(), result.id());
        assertEquals(expectedDTO.name(), result.name());

        verify(categoryValidation).validateAndTrimName(createDTO.name());
        verify(categoryValidation).checkForDuplicateCategoryName(trimmedName, null);
        verify(categoryMapper).toEntityWithTrimmedName(createDTO, trimmedName);
        verify(categoryRepository).save(categoryToSave);
        verify(categoryMapper).toDTO(savedCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, categoryValidation);
    }

    @Test
    @DisplayName("Should throw exception when category name already exists")
    void createCategory_shouldThrowException_whenNameIsDuplicate() {
        CreateCategoryDTO createDTO = new CreateCategoryDTO("Sport", "Wydarzenia sportowe.");
        String trimmedName = "Sport";

        when(categoryValidation.validateAndTrimName(createDTO.name())).thenReturn(trimmedName);
        doThrow(new CategoryAlreadyExistsException("Category with this name already exists.")).when(categoryValidation)
                .checkForDuplicateCategoryName(trimmedName, null);

        CategoryAlreadyExistsException exception = assertThrows(CategoryAlreadyExistsException.class,
                () -> categoryService.createCategory(createDTO),
                "Category with this name already exists.");

        assertEquals("Category with this name already exists.", exception.getMessage());

        verify(categoryValidation).validateAndTrimName(createDTO.name());
        verify(categoryValidation).checkForDuplicateCategoryName(trimmedName, null);
        verifyNoInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Should create category successfully with edge case description length")
    void createCategory_shouldSucceed_withLongDescription() {
        String longDescription = "a".repeat(500);
        CreateCategoryDTO createDTO = new CreateCategoryDTO("Długi Opis", longDescription);
        String trimmedName = "Długi Opis";
        Category categoryToSave = Category.builder().name(trimmedName).description(createDTO.description()).build();
        Category savedCategory = Category.builder().id(1L).name(trimmedName).description(longDescription).build();
        CategoryDTO expectedDTO = new CategoryDTO(1L, trimmedName, longDescription);

        when(categoryValidation.validateAndTrimName(createDTO.name())).thenReturn(trimmedName);
        doNothing().when(categoryValidation).checkForDuplicateCategoryName(trimmedName, null);

        when(categoryMapper.toEntityWithTrimmedName(createDTO, trimmedName)).thenReturn(categoryToSave);
        when(categoryRepository.save(categoryToSave)).thenReturn(savedCategory);
        when(categoryMapper.toDTO(savedCategory)).thenReturn(expectedDTO);

        CategoryDTO result = categoryService.createCategory(createDTO);

        assertNotNull(result);
        assertEquals(longDescription, result.description());

        verify(categoryValidation).validateAndTrimName(createDTO.name());
        verify(categoryValidation).checkForDuplicateCategoryName(trimmedName, null);
        verify(categoryMapper).toEntityWithTrimmedName(createDTO, trimmedName);
        verify(categoryRepository).save(categoryToSave);
        verify(categoryMapper).toDTO(savedCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, categoryValidation);
    }
}
