package com.example.Event_Manager.review.repository;

import com.example.Event_Manager.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    @EntityGraph(attributePaths = {"event", "user"})
    Page<Review> findByEventId(Long eventId, Pageable pageable);

    @EntityGraph(attributePaths = {"event", "user"})
    List<Review> findByEventId(Long eventId);

    Optional<Review> getReviewById(Long reviewId);
}
