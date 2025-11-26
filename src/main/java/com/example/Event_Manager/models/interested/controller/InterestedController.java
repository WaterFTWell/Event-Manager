package com.example.Event_Manager.models.interested.controller;


import com.example.Event_Manager.models._util.annotations.IsAttendee;
import com.example.Event_Manager.models.interested.dto.response.InterestedDTO;
import com.example.Event_Manager.models.interested.service.InterestedService;
import com.example.Event_Manager.models.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interested")
@Tag(name = "Favorite Organizers")
@RequiredArgsConstructor
public class InterestedController {
    private final InterestedService interestedService;

    @Operation(summary = "Toggle interest in an event adding/removing")
    @PostMapping("/{eventId}")
    @IsAttendee
    public ResponseEntity<String> toggleInterest(
            @PathVariable Long eventId,
            @AuthenticationPrincipal User user
    ) {
        String result = interestedService.toggleInterest(user.getId(), eventId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get events user is interested in")
    @GetMapping
    @IsAttendee
    public ResponseEntity<List<InterestedDTO>> getMyInterests(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(interestedService.getUserInterests(user.getId()));
    }
}
