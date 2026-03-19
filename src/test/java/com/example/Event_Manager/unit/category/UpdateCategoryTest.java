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
        Long categoryId = 1L;
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO("Nowa Nazwa", "Nowy opis");
        Category updatedCategory = Category.builder()
                .id(categoryId)
                .name(updateDTO.name())
                .description(updateDTO.description())
                .build();
        CategoryDTO expectedDTO = new CategoryDTO(categoryId, updateDTO.name(), updateDTO.description());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findCategoryByNameIgnoreCase(updateDTO.name())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toDTO(updatedCategory)).thenReturn(expectedDTO);

        CategoryDTO result = categoryService.updateCategory(categoryId, updateDTO);

        assertNotNull(result);
        assertEquals(expectedDTO, result);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findCategoryByNameIgnoreCase(updateDTO.name());
        verify(categoryRepository).save(existingCategory);
        verify(categoryMapper).toDTO(updatedCategory);
    }

    @Test
    @DisplayName("Should update category description while keeping the same name")
    void updateCategory_shouldSucceed_whenOnlyDescriptionChanges() {
        Long categoryId = 1L;
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(existingCategory.getName(), "Zaktualizowany opis");
        Category updatedCategory = Category.builder()
                .id(categoryId)
                .name(existingCategory.getName())
                .description(updateDTO.description())
                .build();
        CategoryDTO expectedDTO = new CategoryDTO(categoryId, existingCategory.getName(), updateDTO.description());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findCategoryByNameIgnoreCase(updateDTO.name())).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toDTO(updatedCategory)).thenReturn(expectedDTO);

        CategoryDTO result = categoryService.updateCategory(categoryId, updateDTO);

        assertNotNull(result);
        assertEquals(expectedDTO, result);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findCategoryByNameIgnoreCase(updateDTO.name());
        verify(categoryRepository).save(existingCategory);
        verify(categoryMapper).toDTO(updatedCategory);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when trying to update non-existent category")
    void updateCategory_shouldThrowException_whenCategoryDoesNotExist() {
        Long nonExistentId = 99L;
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO("Nazwa", "Opis");
        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.updateCategory(nonExistentId, updateDTO));

        assertEquals("Category with ID 99 not found.", exception.getMessage());
        verify(categoryRepository).findById(nonExistentId);
        verify(categoryRepository, never()).findCategoryByNameIgnoreCase(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CategoryAlreadyExistsException when new name is taken by another category")
    void updateCategory_shouldThrowException_whenNameIsTakenByAnotherCategory() {
        Long categoryIdToUpdate = 1L;
        String newName = "Zajęta Nazwa";
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO(newName, "Opis");
        Category otherCategory = Category.builder()
                .id(2L)
                .name(newName)
                .build();

        when(categoryRepository.findById(categoryIdToUpdate)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findCategoryByNameIgnoreCase(newName)).thenReturn(Optional.of(otherCategory));

        CategoryAlreadyExistsException exception = assertThrows(CategoryAlreadyExistsException.class, () ->
                categoryService.updateCategory(categoryIdToUpdate, updateDTO));

        assertEquals("Category with this name already exists.", exception.getMessage());
        verify(categoryRepository).findById(categoryIdToUpdate);
        verify(categoryRepository).findCategoryByNameIgnoreCase(newName);
        verify(categoryRepository, never()).save(any());
    }
}