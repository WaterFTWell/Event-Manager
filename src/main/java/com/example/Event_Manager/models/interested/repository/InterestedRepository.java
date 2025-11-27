package com.example.Event_Manager.models.interested.repository;

import com.example.Event_Manager.models.interested.Interested;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestedRepository extends JpaRepository<Interested, Long> {
    Optional<Interested> findByUserIdAndEventId(Long userId, Long eventId);
    Page<Interested> findAllByUserId(Long userId, Pageable pageable);


}
