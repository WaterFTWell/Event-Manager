package com.example.Event_Manager.models.venue.validation;

import com.example.Event_Manager.models._util.BaseValidation;
import com.example.Event_Manager.models._util.RequestEmptyException;
import com.example.Event_Manager.models.venue.exceptions.VenueNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class VenueValidation implements BaseValidation {
    @Override
    public void checkIfRequestNotNull(Object request) {
        if(request == null) {
            throw new RequestEmptyException("Request cannot be null.");
        }
    }

    @Override
    public void checkIfIdValid(Long id) {
        if(id == null || id <= 0) {
            throw new VenueNotFoundException("Venue with this id is not in database.");
        }
    }

    @Override
    public void checkIfObjectExist(Object object) {
        if(object == null) {
            throw new VenueNotFoundException("Venue not found in database.");
        }
    }
}
