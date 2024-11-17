package org.example.sporteventsapi.dto;

import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.example.sporteventsapi.validator.ValidEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

public record SportEventDTO(
        @Null(message = "ID must be null when creating a new sport event")
        Long id,
        @NotBlank(message = "Name must not be blank")
        String name,
        @ValidEnum(enumClass = SportType.class, message = "Invalid sport type")
        SportType sportType,
        @ValidEnum(enumClass = SportEventStatus.class, message = "Invalid event status")
        SportEventStatus eventStatus,
        @NotNull(message = "StartTime must not be null")
        LocalDateTime startTime) {
}



