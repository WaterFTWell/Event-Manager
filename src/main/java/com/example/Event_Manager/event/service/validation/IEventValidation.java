package com.example.Event_Manager.event.service.validation;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.venue.Venue;

import java.time.LocalDateTime;

public interface IEventValidation {
    String checkEventName(String name);
    String checkOrganizerName(String organizerName);
    Venue findVenueById(Long venueId);
    Category findCategoryById(Long categoryId);
    void validateOrganizerExists(Long organizerId);
    void validateEventDates(LocalDateTime start, LocalDateTime end);
}
