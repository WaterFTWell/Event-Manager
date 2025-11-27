package com.example.Event_Manager.models.venue.controller;

import com.example.Event_Manager.models._util.annotations.IsAdmin;
import com.example.Event_Manager.models.venue.dto.request.CreateVenueDTO;
import com.example.Event_Manager.models.venue.dto.request.UpdateVenueDTO;
import com.example.Event_Manager.models.venue.dto.response.VenueDTO;
import com.example.Event_Manager.models.venue.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
@Validated
public class VenueController implements VenueApi {

    private final VenueService venueService;

    @Override
    @GetMapping
    public ResponseEntity<Page<VenueDTO>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<Long> cities,
            Pageable pageable) {
        return ResponseEntity.ok(venueService.getAll(name, cities, pageable));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<VenueDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getById(id));
    }

    @Override
    @PostMapping
    @IsAdmin
    public ResponseEntity<VenueDTO> create(@RequestBody CreateVenueDTO createVenueDTO) {
        VenueDTO createdVenue = venueService.create(createVenueDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
    }

    @Override
    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<VenueDTO> update(@PathVariable Long id, @RequestBody UpdateVenueDTO updateVenueDTO) {
        VenueDTO updatedVenue = venueService.update(id, updateVenueDTO);
        return ResponseEntity.ok(updatedVenue);
    }

    @Override
    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

