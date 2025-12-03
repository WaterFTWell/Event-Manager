package com.example.Event_Manager.unit.interested;

import com.example.Event_Manager.user.repository.UserRepository;
import com.example.Event_Manager.event.Event;
import com.example.Event_Manager.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.event.repository.EventRepository;
import com.example.Event_Manager.interested.Interested;
import com.example.Event_Manager.interested.dto.response.InterestedDTO;
import com.example.Event_Manager.interested.repository.InterestedRepository;
import com.example.Event_Manager.interested.service.InterestedService;
import com.example.Event_Manager.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Interested Service Unit Test")
public class InterestedServiceTest {
    @Mock
    private InterestedRepository interestedRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private InterestedService interestedService;

    @Test
    @DisplayName("Should add interest when it does not exist")
    void toggleInterest_shouldAdd_whenNotExists() {
        //Given
        Long userId = 1L;
        Long eventId = 100L;
        User user = User.builder().id(userId).build();
        Event event = Event.builder().id(eventId).build();
        // mockujemy ze nie ma jeszcze lajka w bazie
        when(interestedRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        //When
        String result = interestedService.toggleInterest(userId, eventId);

        //Then
        assertEquals("Added to interested", result);
        verify(interestedRepository).save(any(Interested.class));
    }

    @Test
    @DisplayName("Should remove interest when it exists")
    void toggleInterest_shouldRemove_whenExists() {
        //Given
        Long userId = 1L;
        Long eventId = 100L;
        Interested existingInterest = new Interested();
        // mockujemy ze lajk juz jest
        when(interestedRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(Optional.of(existingInterest));

        //When
        String result = interestedService.toggleInterest(userId, eventId);

        //Then
        assertEquals("Removed from interested", result);
        verify(interestedRepository).delete(existingInterest); // sprawdzamy czy usunal
    }

    @Test
    @DisplayName("Should return list of interested events")
    void getUserInterests_shouldReturnPage() {
        //Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Date now = new Date();
        Event event = Event.builder().id(10L).name("Fajny Event").build();
        Interested interested = Interested.builder()
                .event(event)
                .markedAt(now)
                .build();
        Page<Interested> page = new PageImpl<>(List.of(interested));

        when(userRepository.existsById(userId)).thenReturn(true); //serwis sprawdza czy user istnieje
        when(interestedRepository.findAllByUserId(userId, pageable)).thenReturn(page);
        //When
        Page<InterestedDTO> result = interestedService.getUserInterests(userId, pageable);
        //Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Fajny Event", result.getContent().getFirst().eventName());
    }

    @Test
    @DisplayName("Should throw EventNotFoundException when event does not exist")
    void toggleInterest_shouldThrowException_whenEventNotFound() {
        //Given
        Long userId = 1L;
        Long eventId = 999L;

        when(interestedRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        //mockujemy event nie znaleziony,pusty Optional
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        //Then
        assertThrows(EventNotFoundException.class, () ->
            interestedService.toggleInterest(userId, eventId)
        );
    }
    @Test
    @DisplayName("Should return empty list when user has empty list(no interests)")
    void getUserInterests_shouldReturnEmptyList_whenNoInterestsFound() {
        //given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(interestedRepository.findAllByUserId(userId, pageable)).thenReturn(Page.empty());


        //when
        Page<InterestedDTO> result = interestedService.getUserInterests(userId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }
}
