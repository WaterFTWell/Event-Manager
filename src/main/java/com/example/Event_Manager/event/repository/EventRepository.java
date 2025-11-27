package com.example.Event_Manager.event.repository;

import com.example.Event_Manager.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Override
    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city", "venue.city.country"})
    Page<Event> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city", "venue.city.country"})
    Optional<Event> findEventById(Long eventId);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city", "venue.city.country"})
    Page<Event> findByCategory_Id(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city", "venue.city.country"})
    Page<Event> findByVenue_Id(Long venueId, Pageable pageable);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city", "venue.city.country"})
    Page<Event> findByStartTimeBetween(Date startDate, Date endDate, Pageable pageable);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city", "venue.city.country"})
    Page<Event> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city", "venue.city.country"})
    Page<Event> findByOrganizer_Id(Long organizerId, Pageable pageable);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city", "venue.city.country"})
    Page<Event> findByOrganizerFullNameContainingIgnoreCase(String organizerName, Pageable pageable);
}
