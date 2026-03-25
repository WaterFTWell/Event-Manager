package com.example.Event_Manager.category.service.validation;

import org.jspecify.annotations.NonNull;

public interface ICategoryValidation {

    @NonNull String validateAndTrimName(String rawName);
    void checkForDuplicateCategoryName(String name, Long currentCategoryId);
}
