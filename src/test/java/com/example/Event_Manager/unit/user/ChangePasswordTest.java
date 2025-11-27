package com.example.Event_Manager.unit.user;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.dto.request.ChangePasswordRequest;
import com.example.Event_Manager.models.user.service.UserService;
import com.example.Event_Manager.models.user.validation.UserValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Changing Password")
public class ChangePasswordTest {

    @Mock
    private UserRepository userRepository;
    @Mock private UserValidation userValidation;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should change password successfully when credentials are valid")
    void changePassword_Success() {
        //Given
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("stereHaslo", "noweHaslo", "noweHaslo");
        User user = User.builder().id(userId).password("zaszyfrowaneStareHaslo").build();

        doNothing().when(userValidation).checkIfIdValid(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("stereHaslo", "zaszyfrowaneStareHaslo")).thenReturn(true);
        when(passwordEncoder.encode("noweHaslo")).thenReturn("zaszyfrowaneNoweHaslo");

        //When
        userService.changePassword(userId, request);

        //Then
        verify(userRepository).save(user);
        verify(passwordEncoder).encode("noweHaslo");
    }

    @Test
    @DisplayName("Should throw exception when new password and confirmation do not match")
    void changePassword_PasswordsMismatch_ThrowsException() {
        //Given
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("stereHaslo", "noweHaslo", "inneNoweHaslo");

        doNothing().when(userValidation).checkIfIdValid(userId);

        //Then
        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(userId, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when current password is incorrect")
    void changePassword_WrongCurrentPassword_ThrowsException() {
        //Given
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("niepoprawneStareHaslo", "NoweHaslo", "NoweHaslo");
        User user = User.builder().id(userId).password("zaszyfrowaneStareHaslo").build();

        doNothing().when(userValidation).checkIfIdValid(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("niepoprawneStareHaslo", "zaszyfrowaneStareHaslo")).thenReturn(false);

        //Then
        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(userId, request));
        verify(userRepository, never()).save(any());
    }
}