package com.example.Event_Manager.event.repository;

import com.example.Event_Manager.event.Event;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Override
    @EntityGraph(attributePaths = {"category", "venue", "venue.city"})
    @NonNull
    Page<Event> findAll(@NonNull Pageable pageable);

    @Modifying
    @Query("DELETE FROM Event e WHERE e.id = ?1")
    int deleteEventById(Long eventId);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city", "venue.city.country"})
    Optional<Event> findEventById(Long eventId);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city", "venue.city.country"})
    Page<Event> findByCategory_Id(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city", "venue.city.country"})
    Page<Event> findByVenue_Id(Long venueId, Pageable pageable);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city", "venue.city.country"})
    Page<Event> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city"})
    Page<Event> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city"})
    Page<Event> findByOrganizer_Id(Long organizerId, Pageable pageable);

    @EntityGraph(attributePaths = {"organizer", "category", "venue", "venue.city"})
    @Query("SELECT e FROM Event e WHERE " +
            "LOWER(CONCAT(e.organizer.firstName, ' ', e.organizer.lastName)) LIKE LOWER(CONCAT('%', :organizerName, '%'))")
    Page<Event> findByOrganizerFullNameContainingIgnoreCase(@Param("organizerName") String organizerName, Pageable pageable);
}
