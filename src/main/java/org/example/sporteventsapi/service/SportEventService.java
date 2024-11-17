package org.example.sporteventsapi.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.example.sporteventsapi.dto.SportEventDTO;
import org.example.sporteventsapi.mapping.SportEventMapper;
import org.example.sporteventsapi.model.SportEvent;
import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.example.sporteventsapi.repository.SportEventRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j // can be removed
@Service
@AllArgsConstructor
public class SportEventService {

    @NonNull
    private SportEventRepository sportEventRepository;

    public SportEventDTO createSportEvent(SportEventDTO sportEventDTO) {
        var event = this.sportEventRepository.save(SportEventMapper.INSTANCE.toEntity(sportEventDTO));
        return SportEventMapper.INSTANCE.toDto(event);
    }

    public List<SportEventDTO> getSportEvents(SportType type, SportEventStatus status) {
        List<Supplier<List<SportEvent>>> conditions = List.of(
                () -> (type != null && status != null) ? sportEventRepository.findAllByTypeAndStatus(type, status) : null,
                () -> (type != null) ? sportEventRepository.findAllByType(type) : null,
                () -> (status != null) ? sportEventRepository.findAllByStatus(status) : null,
                () -> sportEventRepository.findAll() // Default case
        );

        // Find the first non-null result in the conditions
        return conditions.stream()
                .map(Supplier::get)               // Get the result from the Supplier
                .filter(Objects::nonNull)         // Filter out null results
                .findFirst()                      // Get the first match
                .map(SportEventMapper.INSTANCE::toDtoList) // Map to DTOs
                .orElseGet(Collections::emptyList);  // Return an empty list if no results
    }
}
