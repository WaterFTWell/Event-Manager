package com.example.Event_Manager.models.category.repository;

import com.example.Event_Manager.models.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
