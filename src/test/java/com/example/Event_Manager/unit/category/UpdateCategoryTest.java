package com.example.Event_Manager.unit.category;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.category.dto.response.CategoryDTO;
import com.example.Event_Manager.category.exceptions.CategoryAlreadyExistsException;
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

//    @Mock
//    private CategoryValidation categoryValidation;

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
        Category updatedCategory = Category.builder()
                .id(categoryId)
                .name(updateDTO.name())
                .description(updateDTO.description())
                .build();
        CategoryDTO expectedDTO = new CategoryDTO(categoryId, updateDTO.name(), updateDTO.description());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findCategoryByName(updateDTO.name())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toDTO(updatedCategory)).thenReturn(expectedDTO);

        // When
        CategoryDTO result = categoryService.updateCategory(categoryId, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(expectedDTO, result);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findCategoryByName(updateDTO.name());
        verify(categoryMapper).updateEntity(existingCategory, updateDTO);
        verify(categoryRepository).save(existingCategory);
        verify(categoryMapper).toDTO(updatedCategory);
    }

    @Test
    @DisplayName("Should update category description while keeping the same name")
    void updateCategory_shouldSucceed_whenOnlyDescriptionChanges() {
        // Given
        Long categoryId = 1L;
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(existingCategory.getName(), "Zaktualizowany opis");
        Category updatedCategory = Category.builder()
                .id(categoryId)
                .name(existingCategory.getName())
                .description(updateDTO.description())
                .build();
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

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findCategoryByName(updateDTO.name());
        verify(categoryRepository).save(existingCategory);
        verify(categoryMapper).toDTO(updatedCategory);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when trying to update non-existent category")
    void updateCategory_shouldThrowException_whenCategoryDoesNotExist() {
        // Given
        Long nonExistentId = 99L;
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO("Nazwa", "Opis");
        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.updateCategory(nonExistentId, updateDTO));

        assertEquals("Category with this id is not in database.", exception.getMessage());
        verify(categoryRepository).findById(nonExistentId);
        verify(categoryRepository, never()).findCategoryByName(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CategoryAlreadyExistsException when new name is taken by another category")
    void updateCategory_shouldThrowException_whenNameIsTakenByAnotherCategory() {
        // Given
        Long categoryIdToUpdate = 1L;
        String newName = "Zajęta Nazwa";
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(newName, "Opis");
        Category otherCategory = Category.builder()
                .id(2L)
                .name(newName)
                .build();

        when(categoryRepository.findById(categoryIdToUpdate)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findCategoryByName(newName)).thenReturn(Optional.of(otherCategory));

        // When & Then
        CategoryAlreadyExistsException exception = assertThrows(CategoryAlreadyExistsException.class, () ->
                categoryService.updateCategory(categoryIdToUpdate, updateDTO));

        assertEquals("Category with this name already exists.", exception.getMessage());
        verify(categoryRepository).findById(categoryIdToUpdate);
        verify(categoryRepository).findCategoryByName(newName);
        verify(categoryMapper, never()).updateEntity(any(), any());
        verify(categoryRepository, never()).save(any());
    }
}