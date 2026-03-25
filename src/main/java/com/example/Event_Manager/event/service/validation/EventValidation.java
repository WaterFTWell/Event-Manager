package com.example.Event_Manager.event.service.validation;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.category.repository.CategoryRepository;
import com.example.Event_Manager.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.event.exceptions.InvalidEventDateException;
import com.example.Event_Manager.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.user.repository.UserRepository;
import com.example.Event_Manager.venue.Venue;
import com.example.Event_Manager.venue.exceptions.VenueNotFoundException;
import com.example.Event_Manager.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventValidation implements IEventValidation {

    private final CategoryRepository categoryRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    public String checkEventName(String name) {
        if (name == null) {
            throw new EventNotFoundException("Event name cannot be empty.");
        }

        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new EventNotFoundException("Event name cannot be blank.");
        }

        return trimmed;
    }

    public String checkOrganizerName(String organizerName) {
        if (organizerName == null) {
            throw new EventNotFoundException("Organizer name cannot be empty.");
        }

        String trimmed = organizerName.trim();
        if (trimmed.isEmpty()) {
            throw new EventNotFoundException("Organizer name cannot be blank.");
        }

        return trimmed;
    }


    public Venue findVenueById(Long venueId) {
        if(venueId == null || venueId < 0) {
            throw new VenueNotFoundException("Venue ID must not be null or negative.");
        }

        return venueRepository.findById(venueId)
                .orElseThrow(() -> new VenueNotFoundException("Venue with ID " + venueId + " not found."));
    }

    public Category findCategoryById(Long categoryId) {
        if (categoryId == null || categoryId < 0) {
            throw new CategoryNotFoundException("Category ID must not be null or negative.");
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " not found."));
    }

    public void validateOrganizerExists(Long organizerId) {
        if (organizerId == null || organizerId < 0) {
            throw new UserNotFoundException("Organizer ID must not be null or negative.");
        }

        userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + organizerId + " not found."));
    }

    public void validateEventDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new InvalidEventDateException("Start date and end date must not be null.");
        }

        if (end.isBefore(start)) {
            throw new InvalidEventDateException("End date must not be before start date.");
        }
    }

}
