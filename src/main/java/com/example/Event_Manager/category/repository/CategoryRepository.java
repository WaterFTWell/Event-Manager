package com.example.Event_Manager.category.repository;

import com.example.Event_Manager.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findCategoryByNameIgnoreCase(String name);

    @Modifying
    @Query("DELETE FROM Category c WHERE c.id = ?1")
    int deleteCategoryById(Long categoryId);
}
