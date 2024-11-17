package org.example.sporteventsapi.repository;

import org.example.sporteventsapi.model.SportEvent;
import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportEventRepository extends JpaRepository<SportEvent, Long> {

    List<SportEvent> findAllBySportTypeAndEventStatus(SportType type, SportEventStatus status);

    List<SportEvent> findAllByEventStatus(SportEventStatus status);

    List<SportEvent> findAllBySportType(SportType type);
}
