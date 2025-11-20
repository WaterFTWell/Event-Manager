package com.example.Event_Manager.auth.service;

import com.example.Event_Manager.auth.dto.AuthRequest;
import com.example.Event_Manager.auth.dto.AuthResponse;
import com.example.Event_Manager.auth.dto.RegisterRequest;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.user.enums.Status;
import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.builder()
                    .message("Email already exists")
                    .build();
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return AuthResponse.builder()
                    .message("Phone number already exists")
                    .build();
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.ATTENDEE)
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        var jwtToken = jwtService.generateToken(userDetails);
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole())
                .message("User registered successfully")
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        var jwtToken = jwtService.generateToken(userDetails);
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }
}