package com.example.Event_Manager.models.city.controller;

import com.example.Event_Manager.models._util.annotations.IsAdmin;
import com.example.Event_Manager.models.city.dto.request.CreateCityDTO;
import com.example.Event_Manager.models.city.dto.request.UpdateCityDTO;
import com.example.Event_Manager.models.city.dto.response.CityDTO;
import com.example.Event_Manager.models.city.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
@Validated
public class CityController implements CityApi {

    private final CityService cityService;

    @Override
    @GetMapping
    public ResponseEntity<List<CityDTO>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> countries) {
        return ResponseEntity.ok(cityService.getAll(name, countries));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CityDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cityService.getById(id));
    }

    @Override
    @PostMapping
    @IsAdmin
    public ResponseEntity<CityDTO> create(@Valid @RequestBody CreateCityDTO createCityDTO) {
        CityDTO createdCity = cityService.create(createCityDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCity);
    }

    @Override
    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<CityDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCityDTO updateCityDTO) {
        CityDTO updatedCity = cityService.update(id, updateCityDTO);
        return ResponseEntity.ok(updatedCity);
    }

    @Override
    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

