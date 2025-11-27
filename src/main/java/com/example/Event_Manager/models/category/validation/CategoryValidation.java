package com.example.Event_Manager.models.category.validation;

import com.example.Event_Manager.models._util.BaseValidation;
import com.example.Event_Manager.models._util.RequestEmptyException;
import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.models.category.exceptions.CategoryAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryValidation implements BaseValidation {
    @Override
    public void checkIfRequestNotNull(Object request) {
        if(request == null) {
            throw new RequestEmptyException("Request cannot be null.");
        }
    }

    @Override
    public void checkIfIdValid(Long id) {
        if(id == null || id <= 0) {
            throw new CategoryNotFoundException("Category ID is not valid.");
        }
    }

    @Override
    public void checkIfObjectExist(Object object) {
        if(object == null) {
            throw new CategoryNotFoundException("Category not found in database.");
        }
    }

    public void checkIfNameUnique(Object category) {
        if (category != null) {
            throw new CategoryAlreadyExistsException("Category with this name already exists.");
        }
    }

    public void checkIfNameUniqueForUpdate(Object category, Long categoryId) {
        if (category != null && !categoryId.equals(((Category)category).getId())) {
            throw new CategoryAlreadyExistsException("Category with this name already exists.");
        }
    }
}
