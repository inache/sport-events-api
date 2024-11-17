package org.example.sporteventsapi.dto;

import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record SportEventDTO(String name, SportType type, SportEventStatus status,
                            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime) {
}
