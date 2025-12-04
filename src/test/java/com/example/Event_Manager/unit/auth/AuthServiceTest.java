package com.example.Event_Manager.unit.auth;
import com.example.Event_Manager.auth.dto.request.AuthRequest;
import com.example.Event_Manager.auth.dto.request.RegisterRequest;
import com.example.Event_Manager.auth.dto.response.AuthResponse;
import com.example.Event_Manager.user.repository.UserRepository;
import com.example.Event_Manager.auth.service.AuthService;
import com.example.Event_Manager.auth.util.JwtUtil;
import com.example.Event_Manager.user.User;
import com.example.Event_Manager.user.enums.Role;
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
        RegisterRequest request = new RegisterRequest("Jan", "Janowski", "jan@gmail.com", "123456789", "password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(mock(UserDetails.class));
        when(jwtUtil.generateToken(any())).thenReturn("token_jwt");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("token_jwt", response.getToken());
        assertEquals("User registered successfully", response.getMessage());
        verify(userRepository).save(any());
    }
    @Test
    @DisplayName("Should not register user when email exists")
    void shouldNotRegisterUser_WhenEmailExist() {
        RegisterRequest request = new RegisterRequest("Jan", "Janowski", "zajetyEmail@gmail.com", "123456789", "password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("Email already exists", response.getMessage());
        assertNull(response.getToken());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not register user when phone number exists")
    void shouldNotRegisterUser_WhenPhoneNumberExist() {
        RegisterRequest request = new RegisterRequest("Jan", "Janowski", "janEmail@gmail.com", "123456789", "password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(true);

        AuthResponse response = authService.register(request);

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

        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(jwtUtil.generateToken(any())).thenReturn("token_jwt");

        AuthResponse response = authService.authenticate(request);

        verify(authenticationManager).authenticate(any());
        assertNotNull(response.getToken());
        assertEquals("Login successful", response.getMessage());
    }
    @Test
    @DisplayName("Should throw exception when login password is invalid")
    void shouldThrowException_WhenLoginPasswordIsInvalid() {
        AuthRequest request = new AuthRequest("jan@gmail.com", "zleHaslo");

        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(request);
        });

        verify(jwtUtil, never()).generateToken(any());
    }


}