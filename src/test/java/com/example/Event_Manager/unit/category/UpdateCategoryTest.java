package com.example.Event_Manager.unit.category;

import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;
import com.example.Event_Manager.models.category.exceptions.CategoryNotFoundException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Category Update")
public class UpdateCategoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryValidation categoryValidation;

    @InjectMocks
    private CategoryService categoryService;

    private Category existingCategory;

    @BeforeEach
    void setUp() {
        existingCategory = Category.builder()
                .id(1L)
                .name("Stara Nazwa")
                .description("Stary opis")
                .build();
    }

    @Test
    @DisplayName("Should update category successfully with valid and unique data")
    void updateCategory_shouldSucceed_whenDataIsUniqueAndValid() {
        // Given
        Long categoryId = 1L;
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO("Nowa Nazwa", "Nowy opis");
        Category updatedCategory = Category.builder().id(categoryId).name(updateDTO.name()).description(updateDTO.description()).build();
        CategoryDTO expectedDTO = new CategoryDTO(categoryId, updateDTO.name(), updateDTO.description());

        // Mocking behavior
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findCategoryByName(updateDTO.name())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toDTO(updatedCategory)).thenReturn(expectedDTO);

        // When
        CategoryDTO result = categoryService.updateCategory(categoryId, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(expectedDTO, result);

        verify(categoryValidation).checkIfIdValid(categoryId);
        verify(categoryValidation).checkIfRequestNotNull(updateDTO);
        verify(categoryValidation).checkIfNameUniqueForUpdate(null, categoryId);
        verify(categoryMapper).updateEntity(existingCategory, updateDTO);
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    @DisplayName("Should update category description while keeping the same name")
    void updateCategory_shouldSucceed_whenOnlyDescriptionChanges() {
        // Given
        Long categoryId = 1L;
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(existingCategory.getName(), "Zaktualizowany opis");
        Category updatedCategory = Category.builder().id(categoryId).name(existingCategory.getName()).description(updateDTO.description()).build();
        CategoryDTO expectedDTO = new CategoryDTO(categoryId, existingCategory.getName(), updateDTO.description());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findCategoryByName(updateDTO.name())).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toDTO(updatedCategory)).thenReturn(expectedDTO);

        // When
        CategoryDTO result = categoryService.updateCategory(categoryId, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(expectedDTO, result);

        // Verify that the uniqueness check is performed correctly
        verify(categoryValidation).checkIfNameUniqueForUpdate(existingCategory, categoryId);
        verify(categoryRepository).save(existingCategory);
    }


    @Test
    @DisplayName("Should throw CategoryNotFoundException when trying to update non-existent category")
    void updateCategory_shouldThrowException_whenCategoryDoesNotExist() {
        // Given
        Long nonExistentId = 99L;
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO("Nazwa", "Opis");
        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.updateCategory(nonExistentId, updateDTO);
        });

        assertEquals("Category with this id is not in database.", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CategoryNameAlreadyExistsException when new name is taken by another category")
    void updateCategory_shouldThrowException_whenNameIsTakenByAnotherCategory() {
        // Given
        Long categoryIdToUpdate = 1L;
        String newName = "Zajęta Nazwa";
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(newName, "Opis");
        Category otherCategory = Category.builder().id(2L).name(newName).build();

        when(categoryRepository.findById(categoryIdToUpdate)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findCategoryByName(newName)).thenReturn(Optional.of(otherCategory));
        doThrow(new CategoryNotFoundException("Category with this name already exists."))
                .when(categoryValidation).checkIfNameUniqueForUpdate(otherCategory, categoryIdToUpdate);


        // When & Then
        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.updateCategory(categoryIdToUpdate, updateDTO);
        });

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for a non-positive ID")
    void updateCategory_shouldThrowException_whenIdIsInvalid() {
        // Given
        Long invalidId = -1L;
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO("Nazwa", "Opis");
        doThrow(new IllegalArgumentException("ID must be positive.")).when(categoryValidation).checkIfIdValid(invalidId);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.updateCategory(invalidId, updateDTO);
        });

        verify(categoryRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for a null DTO")
    void updateCategory_shouldThrowException_whenDtoIsNull() {
        // Given
        Long validId = 1L;
        UpdateCategoryDTO nullDto = null;
        doThrow(new IllegalArgumentException("Request cannot be null.")).when(categoryValidation).checkIfRequestNotNull(nullDto);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.updateCategory(validId, nullDto);
        });

        verify(categoryRepository, never()).findById(any());
    }
}