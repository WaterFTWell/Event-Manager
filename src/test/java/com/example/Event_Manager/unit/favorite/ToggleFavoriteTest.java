package com.example.Event_Manager.unit.favorite;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.favorite.Favorite;
import com.example.Event_Manager.models.favorite.exceptions.InvalidFavoriteActionException;
import com.example.Event_Manager.models.favorite.repository.FavoriteRepository;
import com.example.Event_Manager.models.favorite.service.FavoriteService;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.models.user.validation.UserValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Toggling Favorites")
public class ToggleFavoriteTest {

    @Mock private FavoriteRepository favoriteRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserValidation userValidation;

    @InjectMocks private FavoriteService favoriteService;

    @Test
    @DisplayName("Should add to favorites when not already favorited")
    void toggleFavorite_ShouldAdd_WhenNotExists() {
        //Given
        Long userId = 1L;
        Long organizerId = 2L;
        User user = User.builder().id(userId).build();
        User organizer = User.builder().id(organizerId).role(Role.ORGANIZER).build();

        doNothing().when(userValidation).checkIfIdValid(any());
        when(favoriteRepository.findByUserIdAndOrganizerId(userId, organizerId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(organizerId)).thenReturn(Optional.of(organizer));

        //When
        String result = favoriteService.toggleFavorite(userId, organizerId);

        //Then
        assertEquals("Added to favorites", result);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("Should remove from favorites when already favorited")
    void toggleFavorite_ShouldRemove_WhenExists() {
        //Given
        Long userId = 1L;
        Long organizerId = 2L;
        Favorite existingFavorite = new Favorite();

        doNothing().when(userValidation).checkIfIdValid(any());
        when(favoriteRepository.findByUserIdAndOrganizerId(userId, organizerId)).thenReturn(Optional.of(existingFavorite));

        //When
        String result = favoriteService.toggleFavorite(userId, organizerId);

        //Then
        assertEquals("Removed from favorites", result);
        verify(favoriteRepository).delete(existingFavorite);
    }

    @Test
    @DisplayName("Should throw exception when trying to favorite self")
    void toggleFavorite_Self_ThrowsException() {
        Long userId = 1L;
        doNothing().when(userValidation).checkIfIdValid(any());

        assertThrows(InvalidFavoriteActionException.class, () -> favoriteService.toggleFavorite(userId, userId));
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when target is not an organizer")
    void toggleFavorite_NotOrganizer_ThrowsException() {
        //Given
        Long userId = 1L;
        Long targetId = 2L;
        User user = User.builder().id(userId).build();
        User target = User.builder().id(targetId).role(Role.ATTENDEE).build(); //zwykły user

        doNothing().when(userValidation).checkIfIdValid(any());
        when(favoriteRepository.findByUserIdAndOrganizerId(userId, targetId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

        //Then
        assertThrows(InvalidFavoriteActionException.class, () -> favoriteService.toggleFavorite(userId, targetId));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does notexist in database")
    void toggleFavorite_UserNotFound_ThrowsException() {
        //Given
        Long userId = 99L;
        Long organizerId = 2L;

        doNothing().when(userValidation).checkIfIdValid(any());
        when(favoriteRepository.findByUserIdAndOrganizerId(userId, organizerId)).thenReturn(Optional.empty());
        //symulujemy że baza zwraca empty
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class, () -> favoriteService.toggleFavorite(userId, organizerId));
        verify(favoriteRepository, never()).save(any());
    }
    @Test
    @DisplayName("Should throw UserNotFoundException when organizer does notexist in database")
    void toggleFavorite_OrganizerNotFound_ThrowsException() {
        //Given
        Long userId = 1L;
        Long organizerId = 99L;
        User user = User.builder().id(userId).build();

        doNothing().when(userValidation).checkIfIdValid(any());
        when(favoriteRepository.findByUserIdAndOrganizerId(userId, organizerId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        //Symulujemy brak organizatora
        when(userRepository.findById(organizerId)).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class, () -> favoriteService.toggleFavorite(userId, organizerId));
        verify(favoriteRepository, never()).save(any());
    }
}