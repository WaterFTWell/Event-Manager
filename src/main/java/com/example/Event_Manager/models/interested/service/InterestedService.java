package com.example.Event_Manager.models.interested.service;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.interested.Interested;
import com.example.Event_Manager.models.interested.dto.response.InterestedDTO;
import com.example.Event_Manager.models.interested.repository.InterestedRepository;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.models.user.validation.UserValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterestedService {
    private final InterestedRepository interestedRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final UserValidation userValidation;

    @Transactional
    public String toggleInterest(Long userId, Long eventId) {
        userValidation.checkIfIdValid(userId);
        Optional<Interested> existingInterest = interestedRepository.findByUserIdAndEventId(userId, eventId);
        if (existingInterest.isPresent()) {
            //jak jest to usuwamy, czyli uzytkownik odznaczyl
            interestedRepository.delete(existingInterest.get());
            return "Removed from interested";
        } else {
            //jak nie ma, tworzymy nowego
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found" + userId + " not found"));

            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new EventNotFoundException("Event not found" + eventId + " not found"));

            Interested interested = Interested.builder()
                    .user(user)
                    .event(event)
                    .build();

            interestedRepository.save(interested);
            return "Added to interested";
        }
    }
    public Page<InterestedDTO> getUserInterests(Long userId, Pageable pageable) {
        userValidation.checkIfIdValid(userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }

        return interestedRepository.findAllByUserId(userId, pageable)
                .map(i -> new InterestedDTO(
                        i.getEvent().getId(),
                        i.getEvent().getName(),
                        i.getMarkedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                ));
    }
}
