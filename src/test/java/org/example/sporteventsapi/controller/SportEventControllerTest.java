package org.example.sporteventsapi.controller;

import lombok.SneakyThrows;
import org.example.sporteventsapi.AbstractJPAMockTest;
import org.example.sporteventsapi.Application;
import org.example.sporteventsapi.dto.SportEventDTO;
import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.example.sporteventsapi.service.SportEventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SportEvent controller test")
@ContextConfiguration(classes = {Application.class})
@WebMvcTest(value = SportEventController.class)
class SportEventControllerTest extends AbstractJPAMockTest {

    @MockBean
    private SportEventService sportEventService;

    @Test
    @SneakyThrows
    @DisplayName("Verifying GET /sport-events with both type and status")
    void getSportEventsWithTypeAndStatus() {
        // Arrange
        SportType type = SportType.FOOTBALL;
        SportEventStatus status = SportEventStatus.ACTIVE;
        List<SportEventDTO> sportEvents = List.of(
                new SportEventDTO(1L, "Test Event", type, status, LocalDateTime.now())
        );
        when(sportEventService.getSportEvents(type, status)).thenReturn(sportEvents);

        // Act & Assert
        mockMvc.perform(get("/sport-events")
                        .param("type", type.name())
                        .param("status", status.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(sportEventService).getSportEvents(type, status);
    }

    @Test
    @SneakyThrows
    @DisplayName("Verifying GET /sport-events with only status")
    void getSportEventsWithOnlyStatus() {
        // Arrange
        SportEventStatus status = SportEventStatus.ACTIVE;
        List<SportEventDTO> sportEvents = List.of(
                new SportEventDTO(2L, "Test Event", SportType.FOOTBALL, status, LocalDateTime.now())
        );
        when(sportEventService.getSportEvents(null, status)).thenReturn(sportEvents);

        // Act & Assert
        mockMvc.perform(get("/sport-events")
                        .param("status", status.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(sportEventService).getSportEvents(null, status);
    }

    @Test
    @SneakyThrows
    @DisplayName("Verifying GET /sport-events with only type")
    void getSportEventsWithOnlyType() {
        // Arrange
        SportType type = SportType.FOOTBALL;
        List<SportEventDTO> sportEvents = List.of(
                new SportEventDTO(3L, "Test Event", type, SportEventStatus.ACTIVE, LocalDateTime.now())
        );
        when(sportEventService.getSportEvents(type, null)).thenReturn(sportEvents);

        // Act & Assert
        mockMvc.perform(get("/sport-events")
                        .param("type", type.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(sportEventService).getSportEvents(type, null);
    }

    @Test
    @SneakyThrows
    @DisplayName("Verifying GET /sport-events with no parameters")
    void getSportEventsWithNoParameters() {
        // Arrange
        List<SportEventDTO> sportEvents = List.of(
                new SportEventDTO(4L, "Test Event", SportType.FOOTBALL, SportEventStatus.ACTIVE, LocalDateTime.now())
        );
        when(sportEventService.getSportEvents(null, null)).thenReturn(sportEvents);

        // Act & Assert
        mockMvc.perform(get("/sport-events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(sportEventService).getSportEvents(null, null);
    }

    @Test
    @SneakyThrows
    @DisplayName("Verifying GET /sport-events with invalid type, should throw exception")
    void getSportEventsWithInvalidType() {
        // Arrange
        String invalidType = "INVALID_TYPE";

        // Act & Assert
        mockMvc.perform(get("/sport-events")
                        .param("type", invalidType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid value for parameter: type"))
                .andExpect(jsonPath("$.detail").value("The provided value 'INVALID_TYPE' could not be converted to the expected type SportType"));


        verify(sportEventService, never()).getSportEvents(any(), any());
    }

    @Test
    @SneakyThrows
    @DisplayName("Verifying GET /sport-events with invalid eventStatus, should throw exception")
    void getSportEventsWithInvalidEventStatus() {
        // Arrange
        String invalidStatus = "INVALID_STATUS";

        // Act & Assert
        mockMvc.perform(get("/sport-events")
                        .param("status", invalidStatus)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid value for parameter: status"))
                .andExpect(jsonPath("$.detail").value("The provided value 'INVALID_STATUS' could not be converted to the expected type SportEventStatus"));


        verify(sportEventService, never()).getSportEvents(any(), any());
    }

    @Test
    @SneakyThrows
    @DisplayName("Verifying POST /sport-events HTTP request matching")
    void addSportEvent() {
        //Arrange
        var sportEvent = new SportEventDTO(
                null,
                "test",
                SportType.FOOTBALL,
                SportEventStatus.ACTIVE,
                LocalDateTime.of(2024, 11, 16, 15, 30, 45));
        when(sportEventService.createSportEvent(sportEvent)).thenReturn(new SportEventDTO(
                1L,
                "test",
                SportType.FOOTBALL,
                SportEventStatus.ACTIVE,
                LocalDateTime.of(2024, 11, 16, 15, 30, 45)));

        // Act & Assert
        mockMvc.perform(post("/sport-events")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(sportEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.sportType", is(SportType.FOOTBALL.name())))
                .andExpect(jsonPath("$.eventStatus", is(SportEventStatus.ACTIVE.name())))
                .andExpect(jsonPath("$.startTime", is(LocalDateTime.of(2024, 11, 16, 15, 30, 45).toString())));

        verify(sportEventService).createSportEvent(sportEvent);
    }
}