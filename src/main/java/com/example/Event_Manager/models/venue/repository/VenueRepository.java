package com.example.Event_Manager.models.venue.repository;

import com.example.Event_Manager.models.venue.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
