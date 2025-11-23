package com.example.Event_Manager.models.review.repository;

import com.example.Event_Manager.models.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findByEventId(Long eventId);
    List<Review> findByUserId(Long userId);
    Optional<Review> getReviewById(Long reviewId);
}
