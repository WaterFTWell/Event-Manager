package com.example.Event_Manager.models.favorite.service;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.favorite.Favorite;
import com.example.Event_Manager.models.favorite.dto.response.FavoriteDTO;
import com.example.Event_Manager.models.favorite.mapper.FavoriteMapper;
import com.example.Event_Manager.models.favorite.repository.FavoriteRepository;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.models.user.validation.UserValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final FavoriteMapper favoriteMapper;
    private final UserValidation userValidation;

    @Transactional
    public String toggleFavorite(Long userId, Long organizerId) {
        userValidation.checkIfIdValid(userId);
        userValidation.checkIfIdValid(organizerId);

        if (userId.equals(organizerId)) {
            throw new IllegalArgumentException("You cannot add yourself to favorites.");
        }

        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndOrganizerId(userId, organizerId);

        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
            return "Removed from favorites";
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            User organizer = userRepository.findById(organizerId)
                    .orElseThrow(() -> new UserNotFoundException("Organizer not found"));

            if (organizer.getRole() != Role.ORGANIZER) {
                throw new IllegalArgumentException("You can only favorite users with ORGANIZER role.");
            }

            Favorite favorite = Favorite.builder()
                    .user(user)
                    .organizer(organizer)
                    .favoritedAt(new Date())
                    .build();

            favoriteRepository.save(favorite);
            return "Added to favorites";
        }
    }

    public List<FavoriteDTO> getUserFavorites(Long userId) {
        userValidation.checkIfIdValid(userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        return favoriteRepository.findAllByUserId(userId).stream()
                .map(favoriteMapper::toDTO)
                .toList();
    }
}