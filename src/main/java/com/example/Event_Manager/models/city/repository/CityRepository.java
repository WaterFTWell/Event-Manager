package com.example.Event_Manager.models.city.repository;

import com.example.Event_Manager.models.city.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByNameContainingIgnoreCase(String name);
    List<City> findByCountry_CodeIn(List<String> countryCodes);
    List<City> findByNameContainingIgnoreCaseAndCountry_CodeIn(String name, List<String> countryCodes);
}

