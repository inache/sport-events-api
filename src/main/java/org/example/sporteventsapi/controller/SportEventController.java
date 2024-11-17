package org.example.sporteventsapi.controller;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.example.sporteventsapi.dto.SportEventDTO;
import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.example.sporteventsapi.service.SportEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/sport-events")
public class SportEventController {

    @NonNull
    private SportEventService service;

    @GetMapping
    public ResponseEntity<List<SportEventDTO>> getSportEvents(@RequestParam(required = false) SportType type,
                                                              @RequestParam(required = false) SportEventStatus status) {
        return ResponseEntity.ok(service.getSportEvents(type, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SportEventDTO> getSportEventById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSportEventById(id));
    }

    @PostMapping
    public ResponseEntity<SportEventDTO> createSportEvent(@RequestBody SportEventDTO sportEventDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.createSportEvent(sportEventDTO));
    }
}
