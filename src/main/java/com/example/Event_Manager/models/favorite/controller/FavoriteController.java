package com.example.Event_Manager.models.favorite.controller;

import com.example.Event_Manager.models._util.annotations.IsAttendee;
import com.example.Event_Manager.models.favorite.dto.response.FavoriteDTO;
import com.example.Event_Manager.models.favorite.service.FavoriteService;
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
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites Management")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "Toggle favorite organizer (Add/Remove)")
    @PostMapping("/{organizerId}")
    @IsAttendee
    public ResponseEntity<String> toggleFavorite(
            @PathVariable Long organizerId,
            @AuthenticationPrincipal User user
    ) {
        String result = favoriteService.toggleFavorite(user.getId(), organizerId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get list of favorite organizers")
    @GetMapping
    @IsAttendee
    public ResponseEntity<Page<FavoriteDTO>> getMyFavorites(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(user.getId(), pageable));
    }
}
