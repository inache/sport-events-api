package org.example.sporteventsapi.controller;

import lombok.SneakyThrows;
import org.example.sporteventsapi.AbstractJPAMockTest;
import org.example.sporteventsapi.Application;
import org.example.sporteventsapi.dto.SportEventDTO;
import org.example.sporteventsapi.exception.NoRecordFoundException;
import org.example.sporteventsapi.model.SportEvent;
import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.example.sporteventsapi.service.SportEventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SportEvent controller test")
@ContextConfiguration(classes = {Application.class})
@WebMvcTest(value = SportEventController.class)
class SportEventControllerTest extends AbstractJPAMockTest {

    @MockBean
    private SportEventService sportEventService;

    @Nested
    @DisplayName("GET")
    class Get {

        @Test
        @SneakyThrows
        @DisplayName("Verifying GET /sport-events with both type and status")
        void test_1() {
            // Arrange
            var type = SportType.FOOTBALL;
            var status = SportEventStatus.ACTIVE;
            var sportEvents = List.of(SportEventDTO.builder()
                    .id(1L)
                    .name("Test Event")
                    .sportType(type)
                    .eventStatus(status)
                    .startTime(LocalDateTime.now())
                    .build()
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
        void test_2() {
            // Arrange
            var status = SportEventStatus.ACTIVE;
            var sportEvents = List.of(SportEventDTO.builder()
                    .id(2L)
                    .name("Test Event")
                    .sportType(SportType.FOOTBALL)
                    .eventStatus(status)
                    .startTime(LocalDateTime.now())
                    .build()
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
        void test_3() {
            // Arrange
            var type = SportType.FOOTBALL;
            var sportEvents = List.of(SportEventDTO.builder()
                    .id(3L)
                    .name("Test Event")
                    .sportType(SportType.FOOTBALL)
                    .eventStatus(SportEventStatus.ACTIVE)
                    .startTime(LocalDateTime.now())
                    .build()
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
        void test_4() {
            // Arrange
            var sportEvents = List.of(
                    SportEventDTO.builder()
                            .id(4L)
                            .name("Test Event")
                            .sportType(SportType.FOOTBALL)
                            .eventStatus(SportEventStatus.ACTIVE)
                            .startTime(LocalDateTime.now())
                            .build()
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
        void test_5() {
            // Arrange
            var invalidType = "INVALID_TYPE";

            // Act & Assert
            mockMvc.perform(get("/sport-events")
                            .param("type", invalidType)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid value for parameter: type"))
                    .andExpect(jsonPath("$.detail").value("The provided value 'INVALID_TYPE' could not be converted to the expected type 'SportType'!"));


            verify(sportEventService, never()).getSportEvents(any(), any());
        }

        @Test
        @SneakyThrows
        @DisplayName("Verifying GET /sport-events with invalid eventStatus, should throw exception")
        void test_6() {
            // Arrange
            var invalidStatus = "INVALID_STATUS";

            // Act & Assert
            mockMvc.perform(get("/sport-events")
                            .param("status", invalidStatus)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid value for parameter: status"))
                    .andExpect(jsonPath("$.detail").value("The provided value 'INVALID_STATUS' could not be converted to the expected type 'SportEventStatus'!"));

            verify(sportEventService, never()).getSportEvents(any(), any());
        }

        @Test
        @DisplayName("Verifying GET /sport-events by id")
        void test_7() throws Exception {
            // Arrange
            long validId = 1L;
            var mockEvent = SportEventDTO.builder()
                    .id(validId)
                    .name("event-1")
                    .sportType(SportType.FOOTBALL)
                    .eventStatus(SportEventStatus.ACTIVE)
                    .startTime(LocalDateTime.of(2030, 1, 1, 11, 11, 11))
                    .build();
            when(sportEventService.getSportEventById(validId)).thenReturn(mockEvent);

            // Act & Assert
            mockMvc.perform(get("/sport-events/{id}", validId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(validId))
                    .andExpect(jsonPath("$.name").value("event-1"))
                    .andExpect(jsonPath("$.sportType").value("FOOTBALL"))
                    .andExpect(jsonPath("$.eventStatus").value("ACTIVE"))
                    .andExpect(jsonPath("$.startTime").value("2030-01-01T11:11:11"));
        }

        @Test
        @DisplayName("Verifying GET /sport-events by incorrect id should throw exception")
        void test_8() throws Exception {
            // Arrange
            Long invalidId = 99L;
            when(sportEventService.getSportEventById(invalidId)).thenThrow(new NoRecordFoundException("Sport Event", "id", invalidId.toString()));

            // Act & Assert
            mockMvc.perform(get("/sport-events/{id}", invalidId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("No record found!"))
                    .andExpect(jsonPath("$.detail").value("No Sport Event was found by id with value '99'"));
        }
    }

    @Nested
    @DisplayName("POST")
    class Post {

        @Test
        @SneakyThrows
        @DisplayName("Verifying POST /sport-events HTTP request matching")
        void test_1() {
            // Arrange
            var sportEvent = SportEventDTO.builder()
                    .id(null)
                    .name("test")
                    .sportType(SportType.FOOTBALL)
                    .eventStatus(SportEventStatus.ACTIVE)
                    .startTime(LocalDateTime.of(2025, 11, 16, 15, 30, 45))
                    .build();

            when(sportEventService.createSportEvent(sportEvent)).thenReturn(SportEventDTO.builder()
                    .id(1L)
                    .name("test")
                    .sportType(SportType.FOOTBALL)
                    .eventStatus(SportEventStatus.ACTIVE)
                    .startTime(LocalDateTime.of(2025, 11, 16, 15, 30, 45))
                    .build());

            // Act & Assert
            mockMvc.perform(post("/sport-events")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsBytes(sportEvent)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.sportType", is(SportType.FOOTBALL.name())))
                    .andExpect(jsonPath("$.eventStatus", is(SportEventStatus.ACTIVE.name())))
                    .andExpect(jsonPath("$.startTime", is(LocalDateTime.of(2025, 11, 16, 15, 30, 45).toString())));

            verify(sportEventService).createSportEvent(sportEvent);
        }

        @Test
        @SneakyThrows
        @DisplayName("Verifying POST /sport-events HTTP request matching with invalid sportType should throw exception")
        void test_2() {
            // Arrange
            String requestBody = """
                    {
                        "name": "event-2",
                        "sportType": "INVALID_TYPE",
                        "eventStatus": "ACTIVE",
                        "startTime": "2024-11-17T16:43:27"
                    }
                    """;

            // Act & Assert
            mockMvc.perform(post("/sport-events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid Request Body"))
                    .andExpect(jsonPath("$.detail").value(
                            containsString("Invalid request body. Cannot deserialize value 'INVALID_TYPE' to the expected type. Accepted values: [FOOTBALL, HOCKEY, BASKETBALL, BASEBALL, TENNIS, MMA]!")
                    ));
        }
    }

    @Nested
    @DisplayName("PATCH")
    class Patch {

        @Test
        @SneakyThrows
        @DisplayName("Verifying PATCH /sport-events/{id}/status with valid transition from INACTIVE to ACTIVE")
        void test_1() {
            // Arrange
            var sportEvent = SportEvent.builder()
                    .name("Event Name")
                    .sportType(SportType.FOOTBALL)
                    .eventStatus(SportEventStatus.INACTIVE)
                    .startTime(LocalDateTime.now().plusHours(1))
                    .build();
            var updatedEvent = SportEventDTO.builder()
                    .id(1L)
                    .name("Event Name")
                    .sportType(SportType.FOOTBALL)
                    .eventStatus(SportEventStatus.ACTIVE)
                    .startTime(sportEvent.getStartTime())
                    .build();
            Mockito.when(sportEventService.changeEventStatus(1L, SportEventStatus.ACTIVE)).thenReturn(updatedEvent);

            // Act & Assert
            mockMvc.perform(patch("/sport-events/{id}/status", 1L)
                            .param("newStatus", "ACTIVE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Event Name")))
                    .andExpect(jsonPath("$.sportType", is("FOOTBALL")))
                    .andExpect(jsonPath("$.eventStatus", is("ACTIVE")))
                    .andExpect(jsonPath("$.startTime").exists());

            Mockito.verify(sportEventService, times(1)).changeEventStatus(1L, SportEventStatus.ACTIVE);
        }
    }
}