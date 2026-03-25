package com.example.Event_Manager.category.service.validation;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.exceptions.CategoryAlreadyExistsException;
import com.example.Event_Manager.category.exceptions.InvalidCategoryException;
import com.example.Event_Manager.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
final class CategoryValidation implements ICategoryValidation {

    private final CategoryRepository categoryRepository;

    @Contract("null -> fail")
    @NonNull
    public String validateAndTrimName(String rawName) {
        if (rawName == null) {
            throw new InvalidCategoryException("Category name cannot be null.");
        }
        String trimmed = rawName.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidCategoryException("Category name cannot be empty or whitespace only.");
        }
        return trimmed;
    }

    public void checkForDuplicateCategoryName(String name, Long currentCategoryId) {
        Optional<Category> categoryWithSameName = categoryRepository.findCategoryByNameIgnoreCase(name);
        if (categoryWithSameName.isPresent() && !Objects.equals(currentCategoryId, categoryWithSameName.get().getId())) {
            throw new CategoryAlreadyExistsException("Category with this name already exists.");
        }
    }

}
