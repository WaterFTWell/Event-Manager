package com.example.Event_Manager.user.dto.response;

import com.example.Event_Manager.user.enums.Role;

public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Role role
) {}