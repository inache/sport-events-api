package org.example.sporteventsapi.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.example.sporteventsapi.dto.SportEventDTO;
import org.example.sporteventsapi.exception.InvalidStatusChangeException;
import org.example.sporteventsapi.exception.NoRecordFoundException;
import org.example.sporteventsapi.mapping.SportEventMapper;
import org.example.sporteventsapi.model.SportEvent;
import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.example.sporteventsapi.repository.SportEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    public SportEventDTO changeEventStatus(Long id, SportEventStatus newStatus) {
        var now = LocalDateTime.now();

        var sportEvent = sportEventRepository.findById(id)
                .orElseThrow(() -> new NoRecordFoundException("Sport event can not be found"));

        validateStatusChange(sportEvent, newStatus, now);

        sportEvent.setEventStatus(newStatus);
        sportEventRepository.save(sportEvent);

        return SportEventMapper.INSTANCE.toDto(sportEvent);
    }

    private void validateStatusChange(SportEvent sportEvent, SportEventStatus newStatus, LocalDateTime now) {
        switch (newStatus) {
            case INACTIVE:
                validateInactiveStatus(sportEvent);
                break;
            case ACTIVE:
                validateActiveStatus(sportEvent, now);
                break;
            case FINISHED:
                validateFinishedStatus(sportEvent);
                break;
            default:
                throw new InvalidStatusChangeException("Invalid status change");
        }
    }

    private void validateInactiveStatus(SportEvent sportEvent) {
        if (sportEvent.getEventStatus() == SportEventStatus.FINISHED) {
            throw new InvalidStatusChangeException("Cannot change status from FINISHED to INACTIVE");
        }
    }

    private void validateActiveStatus(SportEvent sportEvent, LocalDateTime now) {
        if (sportEvent.getEventStatus() == SportEventStatus.FINISHED) {
            throw new InvalidStatusChangeException("Cannot change status from FINISHED to ACTIVE");
        }
        if (sportEvent.getEventStatus() == SportEventStatus.ACTIVE) {
            throw new InvalidStatusChangeException("Event is already ACTIVE");
        }
        if (sportEvent.getStartTime().isBefore(now)) {
            throw new InvalidStatusChangeException("Cannot activate a sports event that has already started");
        }
    }

    private void validateFinishedStatus(SportEvent sportEvent) {
        if (sportEvent.getEventStatus() == SportEventStatus.FINISHED) {
            throw new InvalidStatusChangeException("Event is already FINISHED");
        }
        if (sportEvent.getEventStatus() == SportEventStatus.INACTIVE) {
            throw new InvalidStatusChangeException("Cannot change status from INACTIVE to FINISHED");
        }
    }
}
