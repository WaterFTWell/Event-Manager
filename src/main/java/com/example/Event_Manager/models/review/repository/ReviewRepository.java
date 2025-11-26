package com.example.Event_Manager.models.review.repository;

import com.example.Event_Manager.models.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    Page<Review> findByEventId(Long eventId, Pageable pageable);

    // aby wyciagnac wszystkie do eventSummary
    List<Review> findByEventId(Long eventId);
    Optional<Review> getReviewById(Long reviewId);
}
