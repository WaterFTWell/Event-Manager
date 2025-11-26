package com.example.Event_Manager.auth.repository;

import com.example.Event_Manager.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE CONCAT(u.firstName, ' ', u.lastName) = :fullName")
    Optional<User> findByFullName(@Param("fullName") String fullName);

}