package com.example.Event_Manager.models.user.controller;

import com.example.Event_Manager.models._util.annotations.IsAdmin;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.dto.request.ChangePasswordRequest;
import com.example.Event_Manager.models.user.dto.request.UpdateUserDTO;
import com.example.Event_Manager.models.user.dto.response.UserDTO;
import com.example.Event_Manager.models.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserProfile(user.getId()));
    }

    @Operation(summary = "Update user profile")
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateUser(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateUserDTO updateDTO
    ) {
        return ResponseEntity.ok(userService.updateUserProfile(user.getId(), updateDTO));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}