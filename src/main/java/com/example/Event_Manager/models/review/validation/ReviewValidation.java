package com.example.Event_Manager.models.review.validation;

import com.example.Event_Manager.models.review.Review;
import com.example.Event_Manager.models.review.exceptions.ReviewNotFoundException;
import com.example.Event_Manager.models._util.BaseValidation;
import com.example.Event_Manager.models._util.RequestEmptyException;
import com.example.Event_Manager.models.review.exceptions.UnauthorizedReviewAccessException;
import com.example.Event_Manager.models.user.User;
import org.springframework.stereotype.Component;

@Component
public class ReviewValidation implements BaseValidation {

    @Override
    public void checkIfRequestNotNull(Object request) {
        if (request == null) {
            throw new RequestEmptyException("Request cannot be null.");
        }
    }

    @Override
    public void checkIfIdValid(Long id) {
        if(id == null || id <= 0) {
            throw new ReviewNotFoundException("Review with this id is not in database.");
        }
    }

    @Override
    public void checkIfObjectExist(Object object) {
        if(object == null) {
            throw new ReviewNotFoundException("Review not found in database.");
        }
    }
}
