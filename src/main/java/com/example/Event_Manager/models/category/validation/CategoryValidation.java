package com.example.Event_Manager.models.category.validation;

import com.example.Event_Manager.models._util.BaseValidation;
import com.example.Event_Manager.models._util.RequestEmptyException;
import com.example.Event_Manager.models.category.exceptions.CategoryNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidation implements BaseValidation {
    @Override
    public void checkIfRequestNotNull(Object request) {
        if(request == null) {
            throw new RequestEmptyException("Request cannot be null.");
        }
    }

    @Override
    public void checkIfIdValid(Long id) {
        if(id == null && id <= 0) {
            throw new CategoryNotFoundException("ID cannot be null.");
        }
    }

    @Override
    public void checkIfObjectExist(Object object) {
        if(object == null) {
            throw new CategoryNotFoundException("Category not found in database.");
        }
    }
}
