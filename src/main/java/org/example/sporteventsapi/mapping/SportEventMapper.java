package org.example.sporteventsapi.mapping;

import org.example.sporteventsapi.dto.SportEventDTO;
import org.example.sporteventsapi.model.SportEvent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SportEventMapper {

    SportEventMapper INSTANCE = Mappers.getMapper(SportEventMapper.class);

    SportEvent toEntity(SportEventDTO sportEventDTO);

    SportEventDTO toDto(SportEvent sportEvent);

    List<SportEventDTO> toDtoList(List<SportEvent> sportEvents);
}
