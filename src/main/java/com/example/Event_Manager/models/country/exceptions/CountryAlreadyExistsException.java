package com.example.Event_Manager.models.country.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CountryAlreadyExistsException extends RuntimeException {
    public CountryAlreadyExistsException(String code) {
        super("Country with code " + code + " already exists");
    }
}
