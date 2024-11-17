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
public class SportEvent extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING) // Store enum as a string in the database
    @Column(name = "type")
    private SportType type;

    @Enumerated(EnumType.STRING) // Store enum as a string in the database
    @Column(name = "status")
    private SportEventStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
