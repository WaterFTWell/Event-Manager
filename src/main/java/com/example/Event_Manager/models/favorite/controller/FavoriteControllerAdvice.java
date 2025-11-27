package com.example.Event_Manager.models.favorite.controller;

import com.example.Event_Manager.models._util.ErrorResponse;
import com.example.Event_Manager.models.favorite.exceptions.InvalidFavoriteActionException;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackageClasses = FavoriteController.class)
public class FavoriteControllerAdvice {

    @ExceptionHandler(InvalidFavoriteActionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAction(InvalidFavoriteActionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now()));
    }
}