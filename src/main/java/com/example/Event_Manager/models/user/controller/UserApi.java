package com.example.Event_Manager.models.user.controller;

import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.dto.request.ChangePasswordRequest;
import com.example.Event_Manager.models.user.dto.request.UpdateUserDTO;
import com.example.Event_Manager.models.user.dto.response.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(name = "User Management")
public interface UserApi {

    @Operation(summary = "Get current user")
    ResponseEntity<UserDTO> getCurrentUser(User user);

    @Operation(summary = "Update current user")
    ResponseEntity<UserDTO> updateUser(User user, @Valid UpdateUserDTO updateDTO);

    @Operation(summary = "Change password")
    ResponseEntity<Void> changePassword(User user, @Valid ChangePasswordRequest request);

    @Operation(summary = "Delete user only Admin")
    ResponseEntity<Void> deleteUser(Long id);
}