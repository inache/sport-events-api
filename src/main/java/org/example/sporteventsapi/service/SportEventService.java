package org.example.sporteventsapi.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.example.sporteventsapi.dto.SportEventDTO;
import org.example.sporteventsapi.exception.NoRecordFoundException;
import org.example.sporteventsapi.mapping.SportEventMapper;
import org.example.sporteventsapi.model.SportEvent;
import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.example.sporteventsapi.repository.SportEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class SportEventService {

    @NonNull
    private SportEventRepository sportEventRepository;

    @Transactional
    public SportEventDTO createSportEvent(SportEventDTO sportEventDTO) {
        var event = this.sportEventRepository.save(SportEventMapper.INSTANCE.toEntity(sportEventDTO));
        return SportEventMapper.INSTANCE.toDto(event);
    }

    public List<SportEventDTO> getSportEvents(SportType type, SportEventStatus status) {
        List<Supplier<List<SportEvent>>> conditions = List.of(
                () -> (type != null && status != null) ? sportEventRepository.findAllBySportTypeAndEventStatus(type, status) : null,
                () -> (type != null) ? sportEventRepository.findAllBySportType(type) : null,
                () -> (status != null) ? sportEventRepository.findAllByEventStatus(status) : null,
                () -> sportEventRepository.findAll()
        );

        return conditions.stream()
                .map(Supplier::get)
                .filter(Objects::nonNull)
                .findFirst()
                .map(SportEventMapper.INSTANCE::toDtoList)
                .orElseGet(Collections::emptyList);
    }

    public SportEventDTO getSportEventById(Long id) {
        var sportEvent = sportEventRepository.findById(id).orElseThrow(() -> new NoRecordFoundException(String.format("Sport event with id [%s] was not found", id)));
        return SportEventMapper.INSTANCE.toDto(sportEvent);
    }
}
