package com.example.Event_Manager.models.venue.repository;

import com.example.Event_Manager.models.venue.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    Page<Venue> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Venue> findByCity_IdIn(List<Long> cityIds, Pageable pageable);
    Page<Venue> findByNameContainingIgnoreCaseAndCity_IdIn(String name, List<Long> cityIds, Pageable pageable);
}

