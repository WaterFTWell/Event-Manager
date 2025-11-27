package com.example.Event_Manager.models.country.controller;

import com.example.Event_Manager.models._util.annotations.IsAdmin;
import com.example.Event_Manager.models.country.dto.request.CreateCountryDTO;
import com.example.Event_Manager.models.country.dto.request.UpdateCountryDTO;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import com.example.Event_Manager.models.country.service.CountryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
@Validated
public class CountryController implements CountryApi {

    private final CountryService countryService;

    @Override
    @PostMapping
    @IsAdmin
    public ResponseEntity<CountryDTO> create(@Valid @RequestBody CreateCountryDTO createCountryDTO) {
        CountryDTO createdCountry = countryService.create(createCountryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCountry);
    }

    @Override
    @PutMapping("/{code}")
    @IsAdmin
    public ResponseEntity<CountryDTO> update(
            @PathVariable String code,
            @Valid @RequestBody UpdateCountryDTO updateCountryDTO) {
        CountryDTO updatedCountry = countryService.update(code, updateCountryDTO);
        return ResponseEntity.ok(updatedCountry);
    }

    @Override
    @DeleteMapping("/{code}")
    @IsAdmin
    public ResponseEntity<Void> delete(@PathVariable String code) {
        countryService.delete(code);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/{code}")
    public ResponseEntity<CountryDTO> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(countryService.getByCode(code));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<CountryDTO>> getAll() {
        return ResponseEntity.ok(countryService.getAll());
    }
}
