package com.example.Event_Manager.models.category.exceptions;

public class CategoriesNotFoundException extends RuntimeException {
    public CategoriesNotFoundException(String message) {
        super(message);
    }
}
