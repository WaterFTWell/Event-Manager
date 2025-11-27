package com.example.Event_Manager.unit.user;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.dto.request.UpdateUserDTO;
import com.example.Event_Manager.models.user.dto.response.UserDTO;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.models.user.mapper.UserMapper;
import com.example.Event_Manager.models.user.service.UserService;
import com.example.Event_Manager.models.user.validation.UserValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for User Update")
public class UpdateUserTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private UserValidation userValidation;

    @InjectMocks private UserService userService;

    @Test
    @DisplayName("Should update user profile successfully")
    void updateUser_Success() {
        //Given
        Long userId = 1L;
        UpdateUserDTO updateDTO = new UpdateUserDTO("Nowy", "Kowalski", "987654321");
        User existingUser = User.builder().id(userId).firstName("Stary").build();
        UserDTO expectedResponse = new UserDTO(userId, "Nowy", "Kowalski", "email@email.email", "987654321", Role.ATTENDEE);

        doNothing().when(userValidation).checkIfIdValid(userId);
        doNothing().when(userValidation).checkIfRequestNotNull(updateDTO);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        //save zwraca zaktualizowanego usera
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toDTO(existingUser)).thenReturn(expectedResponse);

        //When
        UserDTO result = userService.updateUserProfile(userId, updateDTO);

        //Then
        assertNotNull(result);
        assertEquals("Nowy", result.firstName());

        //weryfikujemy czy mapper przepisał dane z DTO do encji
        verify(userMapper).updateEntity(existingUser, updateDTO);
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Should throw exception when user to update does not exist")
    void updateUser_UserNotFound_ThrowsException() {
        //Given
        Long userId = 99L;
        UpdateUserDTO updateDTO = new UpdateUserDTO("A", "B", "123");

        doNothing().when(userValidation).checkIfIdValid(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class, () -> userService.updateUserProfile(userId, updateDTO));
        verify(userRepository, never()).save(any());
    }
}