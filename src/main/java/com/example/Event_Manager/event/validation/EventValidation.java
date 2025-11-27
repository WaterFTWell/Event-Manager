package com.example.Event_Manager.event.validation;

import com.example.Event_Manager.event.Event;
import com.example.Event_Manager.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.event.exceptions.EventsNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class EventValidation {

    public void checkIfEventPageEmpty(Page<Event> eventsPage){
        if (eventsPage.isEmpty()) {
            throw new EventsNotFoundException("No events found in database.");
        }
    }
    public void checkEventName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new EventNotFoundException("Event name cannot be empty or blank.");
        }
    }
    public void checkOrganizerName(String organizerName) {
        if (organizerName == null || organizerName.trim().isEmpty()) {
            throw new EventNotFoundException("Organizer name cannot be empty or blank.");
        }
    }

}
