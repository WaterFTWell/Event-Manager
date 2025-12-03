package com.example.Event_Manager.category.service;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.category.dto.response.CategoryDTO;
import com.example.Event_Manager.category.exceptions.CategoriesNotFoundException;
import com.example.Event_Manager.category.exceptions.CategoryAlreadyExistsException;
import com.example.Event_Manager.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.category.mapper.CategoryMapper;
import com.example.Event_Manager.category.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDTO createCategory(CreateCategoryDTO createCategoryDTO) {

        Optional<Category> existingCategory = categoryRepository.findCategoryByName(createCategoryDTO.name());
        if (existingCategory.isPresent()) {
            throw new CategoryAlreadyExistsException("Category with this name already exists.");
        }

        Category category = categoryMapper.toEntity(createCategoryDTO);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long categoryId, UpdateCategoryDTO updateCategoryDTO) {

        Category categoryToUpdate = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with this id is not in database."));

        Optional<Category> categoryWithSameName = categoryRepository.findCategoryByName(updateCategoryDTO.name());
        if (categoryWithSameName.isPresent() && !categoryWithSameName.get().getId().equals(categoryId)) {
            throw new CategoryAlreadyExistsException("Category with this name already exists.");
        }

        categoryMapper.updateEntity(categoryToUpdate, updateCategoryDTO);
        Category updatedCategory = categoryRepository.save(categoryToUpdate);

        return categoryMapper.toDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Category with this id is not in database.");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found in database."));

        return categoryMapper.toDTO(category);
    }

    @Override
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        if (categories.isEmpty()) {
            throw new CategoriesNotFoundException("No categories found in database.");
        }
        return categories.map(categoryMapper::toDTO);
    }
}
