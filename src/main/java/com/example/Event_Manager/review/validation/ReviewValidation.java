package com.example.Event_Manager.review.validation;

import com.example.Event_Manager.review.Review;
import com.example.Event_Manager.review.exceptions.ReviewsNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewValidation {

    public void checkIfReviewsEmpty(Page<Review> reviewsPage) {
        if (reviewsPage == null || reviewsPage.isEmpty()) {
            throw new ReviewsNotFoundException("No reviews found");
        }
    }
    public void checkIfReviewsListEmpty(List<Review> reviewsPage) {
        if (reviewsPage == null || reviewsPage.isEmpty()) {
            throw new ReviewsNotFoundException("No reviews summary found for ");
        }
    }
}
