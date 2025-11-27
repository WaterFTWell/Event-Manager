package com.example.Event_Manager.models.favorite.repository;

import com.example.Event_Manager.models.favorite.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndOrganizerId(Long userId, Long organizerId);
    Page<Favorite> findAllByUserId(Long userId, Pageable pageable);
}