package com.example.Event_Manager.models.user.validation;

import com.example.Event_Manager.models._util.BaseValidation;
import com.example.Event_Manager.models._util.RequestEmptyException;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserValidation implements BaseValidation {


    @Override
    public void checkIfRequestNotNull(Object request) {
        if(request == null) {
            throw new RequestEmptyException("Request cannot be null.");
        }
    }

    @Override
    public void checkIfIdValid(Long id) {
        if(id == null || id <= 0) {
            throw new UserNotFoundException("User with this id is not in database.");
        }
    }

    @Override
    public void checkIfObjectExist(Object object) {
        if(object == null) {
            throw new UserNotFoundException("User not found in database.");
        }
    }
}
