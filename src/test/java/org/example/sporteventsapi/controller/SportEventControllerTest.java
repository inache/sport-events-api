package org.example.sporteventsapi.controller;

import lombok.SneakyThrows;
import org.example.sporteventsapi.AbstractJPAMockTest;
import org.example.sporteventsapi.Application;
import org.example.sporteventsapi.config.NoSecurityConfiguration;
import org.example.sporteventsapi.config.NonSecuredConfig;
import org.example.sporteventsapi.dto.SportEventDTO;
import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.example.sporteventsapi.service.SportEventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SportEvent controller test")
@ContextConfiguration(classes = {Application.class, NonSecuredConfig.class})
@WebMvcTest(value = SportEventController.class)
@ActiveProfiles("non-secured-test-profile")
class SportEventControllerTest extends AbstractJPAMockTest {

    @MockBean
    private SportEventService sportEventService;

    @Test
    @SneakyThrows
    @DisplayName("Verifying POST /sport-event HTTP request matching")
    void addSportEvent() {
        //Arrange
        var sportEvent = new SportEventDTO(
                "test",
                SportType.FOOTBALL,
                SportEventStatus.ACTIVE,
                LocalDateTime.of(2024, 11, 16, 15, 30, 45));

        // Act & Assert
        mockMvc.perform(post("/sport-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sportEvent)))
                .andExpect(status().isCreated());

        verify(sportEventService).createSportEvent(sportEvent);
    }

    @Test
    @SneakyThrows
    @DisplayName("Verifying GET /sport-events with both type and status")
    void getSportEventsWithTypeAndStatus() {
        // Arrange
        SportType type = SportType.FOOTBALL;
        SportEventStatus status = SportEventStatus.ACTIVE;
        List<SportEventDTO> sportEvents = List.of(
                new SportEventDTO("Test Event", type, status, LocalDateTime.now())
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
                new SportEventDTO("Test Event", SportType.FOOTBALL, status, LocalDateTime.now())
        );
        when(sportEventService.getSportEvents(null, status)).thenReturn(sportEvents);

        // Act & Assert
        mockMvc.perform(get("/sport-events")
                        .param("status", status.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));  // Verifies there is one event returned

        // Verify the service method was called with only the status
        verify(sportEventService).getSportEvents(null, status);
    }

    @Test
    @SneakyThrows
    @DisplayName("Verifying GET /sport-events with no parameters")
    void getSportEventsWithNoParameters() {
        // Arrange
        List<SportEventDTO> sportEvents = List.of(
                new SportEventDTO("Test Event", SportType.FOOTBALL, SportEventStatus.ACTIVE, LocalDateTime.now())
        );
        when(sportEventService.getSportEvents(null, null)).thenReturn(sportEvents);

        // Act & Assert
        mockMvc.perform(get("/sport-events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));  // Verifies there is one event returned

        // Verify the service method was called with no parameters
        verify(sportEventService).getSportEvents(null, null);
    }

    @Test
    @SneakyThrows
    @DisplayName("Verifying GET /sport-events with invalid type")
    void getSportEventsWithInvalidType() {
        // Arrange
        String invalidType = "INVALID_TYPE";

        // Act & Assert
        mockMvc.perform(get("/sport-events")
                        .param("type", invalidType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Or 404 if not found or another error

        // Verify the service was not called due to invalid input
        verify(sportEventService, never()).getSportEvents(any(), any());
    }
}