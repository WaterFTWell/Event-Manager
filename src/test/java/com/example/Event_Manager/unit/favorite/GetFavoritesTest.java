package com.example.Event_Manager.unit.favorite;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.favorite.Favorite;
import com.example.Event_Manager.models.favorite.dto.response.FavoriteDTO;
import com.example.Event_Manager.models.favorite.mapper.FavoriteMapper;
import com.example.Event_Manager.models.favorite.repository.FavoriteRepository;
import com.example.Event_Manager.models.favorite.service.FavoriteService;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.models.user.validation.UserValidation;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Getting Favorites")
public class GetFavoritesTest {

    @Mock private FavoriteRepository favoriteRepository;
    @Mock private UserRepository userRepository;
    @Mock private FavoriteMapper favoriteMapper;
    @Mock private UserValidation userValidation;

    @InjectMocks private FavoriteService favoriteService;

    @Test
    @DisplayName("Should return list of favorites")
    void getUserFavorites_Success() {
        //Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        User organizer = User.builder().id(2L).firstName("Org").lastName("One").email("org@test.com").build();
        Favorite fav = Favorite.builder().organizer(organizer).build();
        FavoriteDTO dto = new FavoriteDTO(2L, "Org One", "org@test.com", null);

        Page<Favorite> favoritePage = new PageImpl<>(List.of(fav));

        doNothing().when(userValidation).checkIfIdValid(userId);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(favoriteRepository.findAllByUserId(userId, pageable)).thenReturn(favoritePage);
        when(favoriteMapper.toDTO(fav)).thenReturn(dto);

        //When
        Page<FavoriteDTO> result = favoriteService.getUserFavorites(userId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Org One", result.getContent().get(0).organizerName());
    }

    @Test
    @DisplayName("Should return empty list when user has no favorites")
    void getUserFavorites_EmptyList_WhenNoFavorites() {
        //Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        doNothing().when(userValidation).checkIfIdValid(userId);
        //User istnieje
        when(userRepository.existsById(userId)).thenReturn(true);
        //ale nie posiada ulubionych organizatorów
        when(favoriteRepository.findAllByUserId(userId, pageable)).thenReturn(Page.empty());

        //When
        Page<FavoriteDTO> result = favoriteService.getUserFavorites(userId, pageable);

        //Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(favoriteMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when request favorite for nonexistent user")
    void getUserFavorites_UserNotFound_ThrowsException() {
        //Given
        Long userId = 99L;
        Pageable pageable = PageRequest.of(0, 10);

        doNothing().when(userValidation).checkIfIdValid(userId);
        when(userRepository.existsById(userId)).thenReturn(false); //User nie istnieje

        //When & Then
        assertThrows(UserNotFoundException.class, () -> favoriteService.getUserFavorites(userId, pageable));
        verify(favoriteRepository, never()).findAllByUserId(any(), any());
    }
}