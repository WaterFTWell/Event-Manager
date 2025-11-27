package com.example.Event_Manager.unit.user;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.models.user.service.UserService;
import com.example.Event_Manager.models.user.validation.UserValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Deleting User")
public class DeleteUserTest {

    @Mock private UserRepository userRepository;
    @Mock private UserValidation userValidation;

    @InjectMocks private UserService userService;

    @Test
    @DisplayName("Should delete user successfully when user exists")
    void deleteUser_Success() {
        //Given
        Long userId = 1L;
        doNothing().when(userValidation).checkIfIdValid(userId);
        when(userRepository.existsById(userId)).thenReturn(true);

        //When
        userService.deleteUser(userId);
        //Then
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting nonexistent user")
    void deleteUser_NotFound_ThrowsException() {
        //Given
        Long userId = 999L;
        doNothing().when(userValidation).checkIfIdValid(userId);
        when(userRepository.existsById(userId)).thenReturn(false);

        //Then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(any());
    }
}