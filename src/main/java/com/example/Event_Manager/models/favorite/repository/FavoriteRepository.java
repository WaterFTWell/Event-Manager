package com.example.Event_Manager.models.favorite.repository;

import com.example.Event_Manager.models.favorite.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndOrganizerId(Long userId, Long organizerId);
    List<Favorite> findAllByUserId(Long userId);
}