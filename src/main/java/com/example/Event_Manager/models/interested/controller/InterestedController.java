package com.example.Event_Manager.models.interested.controller;


import com.example.Event_Manager.models._util.annotations.IsAttendee;
import com.example.Event_Manager.models.interested.dto.response.InterestedDTO;
import com.example.Event_Manager.models.interested.service.InterestedService;
import com.example.Event_Manager.models.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<Page<InterestedDTO>> getMyInterests(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(interestedService.getUserInterests(user.getId(), pageable));
    }
}
