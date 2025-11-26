package com.example.Event_Manager.unit.auth;
import com.example.Event_Manager.auth.dto.request.AuthRequest;
import com.example.Event_Manager.auth.dto.request.RegisterRequest;
import com.example.Event_Manager.auth.dto.response.AuthResponse;
import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.auth.service.AuthService;
import com.example.Event_Manager.auth.util.JwtUtil;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Tests")
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should register user when data is valid")
    void shouldRegisterUser_WhenDataIsValid() {
        //Given
        RegisterRequest request = new RegisterRequest("Jan", "Janowski", "jan@gmail.com", "123456789", "password123", Role.ATTENDEE);

        //mockujemy nie istnienie uzytkownika o podanym emailu i numerze telefonu
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

        //mockujemy generowanie tokena
        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(mock(UserDetails.class));
        when(jwtUtil.generateToken(any())).thenReturn("token_jwt");

        //when
        AuthResponse response = authService.register(request);

        //then
        assertNotNull(response);
        assertEquals("token_jwt", response.getToken());
        assertEquals("User registered successfully", response.getMessage());
        verify(userRepository).save(any());
    }
    @Test
    @DisplayName("Should not register user when email exists")
    void shouldNotRegisterUser_WhenEmailExist() {
        //Given
        RegisterRequest request = new RegisterRequest("Jan", "Janowski", "zajetyEmail@gmail.com", "123456789", "password123", Role.ATTENDEE);

        //mockujemy ze istnieje uzytkownika o podanym emailu
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        //When
        AuthResponse response = authService.register(request);

        //Then
        assertNotNull(response);
        assertEquals("Email already exists", response.getMessage());
        assertNull(response.getToken());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not register user when phone number exists")
    void shouldNotRegisterUser_WhenPhoneNumberExist() {
        //Given
        RegisterRequest request = new RegisterRequest("Jan", "Janowski", "janEmail@gmail.com", "123456789", "password123", Role.ATTENDEE);

        //mockujemy ze istnieje uzytkownika o podanym nr telefonu
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(true);

        //When
        AuthResponse response = authService.register(request);

        //Then
        assertNotNull(response);
        assertEquals("Phone number already exists", response.getMessage());
        assertNull(response.getToken());
        verify(userRepository, never()).save(any());
    }
    @Test
    @DisplayName("Should login user when credentials are valid")
    void shouldLogin_WhenCredentialsAreValid(){
        AuthRequest request = new AuthRequest("jan@gmail.com", "password123");
        User user = User.builder()
                .email("jan@gmail.com")
                .role(Role.ATTENDEE)
                .build();

        //mockujemy dzialanie uwierzytelnienia
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(mock(UserDetails.class));
        when(jwtUtil.generateToken(any())).thenReturn("token_jwt");

        //When
        AuthResponse response = authService.authenticate(request);

        //Then
        verify(authenticationManager).authenticate(any());
        assertNotNull(response.getToken());
        assertEquals("Login successful", response.getMessage());
    }
    @Test
    @DisplayName("Should throw exception when login password is invalid")
    void shouldThrowException_WhenLoginPasswordIsInvalid() {
        //Given
        AuthRequest request = new AuthRequest("jan@gmail.com", "zleHaslo");

        //mockujemy ze menadzer uwierzytelniania rzuca wyjatek przy zlym hasle
        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager).authenticate(any());

        //When
        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(request);
        });

        //Then
        verify(jwtUtil, never()).generateToken(any());
    }


}