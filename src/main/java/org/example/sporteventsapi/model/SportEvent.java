package org.example.sporteventsapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "sport_events")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SportEvent extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "sport_type")
    @Enumerated(EnumType.STRING)
    private SportType sportType;

    @Column(name = "event_status")
    @Enumerated(EnumType.STRING)
    private SportEventStatus eventStatus;

    @Column(name = "start_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime startTime;
}
