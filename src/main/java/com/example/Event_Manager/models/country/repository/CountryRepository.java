package com.example.Event_Manager.models.country.repository;

import com.example.Event_Manager.models.country.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {}
