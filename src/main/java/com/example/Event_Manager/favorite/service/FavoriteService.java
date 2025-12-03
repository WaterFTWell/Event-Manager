package com.example.Event_Manager.favorite.service;

import com.example.Event_Manager.user.repository.UserRepository;
import com.example.Event_Manager.favorite.Favorite;
import com.example.Event_Manager.favorite.dto.response.FavoriteDTO;
import com.example.Event_Manager.favorite.exceptions.InvalidFavoriteActionException;
import com.example.Event_Manager.favorite.mapper.FavoriteMapper;
import com.example.Event_Manager.favorite.repository.FavoriteRepository;
import com.example.Event_Manager.user.User;
import com.example.Event_Manager.user.enums.Role;
import com.example.Event_Manager.user.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final FavoriteMapper favoriteMapper;

    @Transactional
    public String toggleFavorite(Long userId, Long organizerId) {

        //użytkownik nie moze dodać samego siebie do ulubionych
        if (userId.equals(organizerId)) {
            throw new InvalidFavoriteActionException("You cannot add yourself to favorites.");
        }

        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndOrganizerId(userId, organizerId);

        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
            return "Removed from favorites";
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found" + userId + " not found"));

            User organizer = userRepository.findById(organizerId)
                    .orElseThrow(() -> new UserNotFoundException("Organizer not found" + userId + " not found"));

            //walidacja czy "organizator" ma role organizatora
            if (organizer.getRole() != Role.ORGANIZER) {
                throw new InvalidFavoriteActionException("You can only favorite users with ORGANIZER role.");
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

    public Page<FavoriteDTO> getUserFavorites(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found" + userId + " not found");
        }

        return favoriteRepository.findAllByUserId(userId, pageable)
                .map(favoriteMapper::toDTO);
    }
}