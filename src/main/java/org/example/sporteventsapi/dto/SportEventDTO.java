package org.example.sporteventsapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.example.sporteventsapi.validator.ValidEnum;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SportEventDTO {
    @Null(message = "ID must be null when creating a new sport event")
    Long id;

    @Valid
    @NotBlank(message = "Name must not be blank")
    String name;

    @ValidEnum(enumClass = SportType.class, message = "Invalid sport type")
    @NotNull
    SportType sportType;

    @ValidEnum(enumClass = SportEventStatus.class, message = "Invalid event status")
    @NotNull
    SportEventStatus eventStatus;

    @NotNull(message = "StartTime must not be null")
    LocalDateTime startTime;
}



