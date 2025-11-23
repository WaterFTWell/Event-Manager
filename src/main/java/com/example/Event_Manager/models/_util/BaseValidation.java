package com.example.Event_Manager.models._util;

public interface BaseValidation {

    // powinien rzucac RequestEmptyException
    void checkIfRequestNotNull(Object request);

    void checkIfIdValid(Long id);
    void checkIfObjectExist(Object object);
}
