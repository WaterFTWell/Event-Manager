package com.example.Event_Manager.models.event.repository;

import com.example.Event_Manager.models.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findEventById(Long eventId);

    Page<Event> findByCategory_Id(Long categoryId, Pageable pageable);

    Page<Event> findByVenue_Id(Long venueId, Pageable pageable);

    Page<Event> findByStartTimeBetween(Date startDate, Date endDate, Pageable pageable);

    Page<Event> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Event> findByOrganizer_Id(Long organizerId, Pageable pageable);

    Page<Event> findByOrganizerFullNameContainingIgnoreCase(String organizerName, Pageable pageable);
}
