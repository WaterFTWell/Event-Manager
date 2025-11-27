package com.example.Event_Manager.models.favorite.exceptions;

public class InvalidFavoriteActionException extends RuntimeException {
    public InvalidFavoriteActionException(String message) {
        super(message);
    }
}