package com.example.Event_Manager.models.user.dto.response;

import com.example.Event_Manager.models.user.enums.Role;

public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Role role
) {}