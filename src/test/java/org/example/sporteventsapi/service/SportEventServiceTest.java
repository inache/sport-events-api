package org.example.sporteventsapi.service;

import lombok.SneakyThrows;
import org.example.sporteventsapi.dto.SportEventDTO;
import org.example.sporteventsapi.exception.InvalidStatusChangeException;
import org.example.sporteventsapi.exception.NoRecordFoundException;
import org.example.sporteventsapi.model.SportEvent;
import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.example.sporteventsapi.repository.SportEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SportEventServiceTest {
    @Mock
    private SportEventRepository sportEventRepository;

    @InjectMocks
    private SportEventService sportEventService;

    @Nested
    @DisplayName("Create sport event")
    class CreateSportEvent {
        @Test
        @SneakyThrows
        @DisplayName("Successfully create sport event")
        void test_1() {
            // Arrange
            var sportEventDTO = new SportEventDTO(null, "Test Event", SportType.FOOTBALL, SportEventStatus.ACTIVE,
                    LocalDateTime.of(2025, 11, 16, 15, 30, 45));
            var createdSportEvent = new SportEvent("Test Event", SportType.FOOTBALL, SportEventStatus.ACTIVE,
                    LocalDateTime.of(2025, 11, 16, 15, 30, 45));
            createdSportEvent.setId(2L);

            when(sportEventRepository.save(any(SportEvent.class))).thenReturn(createdSportEvent);

            // Act
            var result = sportEventService.createSportEvent(sportEventDTO);

            // Assert
            assertNotNull(result);
            assertEquals(2L, result.getId());
            assertEquals("Test Event", result.getName());
            assertEquals(SportType.FOOTBALL, result.getSportType());
            assertEquals(SportEventStatus.ACTIVE, result.getEventStatus());
            verify(sportEventRepository).save(any(SportEvent.class));
        }
    }

    @ParameterizedTest
    @DisplayName("Test invalid status change")
    @CsvSource({
            "INACTIVE, FINISHED, Invalid status change from 'INACTIVE' to 'FINISHED'",
            "ACTIVE, ACTIVE, Event is already ACTIVE",
            "FINISHED, INACTIVE, Invalid status change from 'FINISHED' to 'INACTIVE'",
            "FINISHED, ACTIVE, Invalid status change from 'FINISHED' to 'ACTIVE'"
    })
    public void testInvalidStatusChange(SportEventStatus currentStatus, SportEventStatus newStatus, String expectedMessage) {
        // Arrange
        var sportEvent = new SportEvent("Test Event", SportType.FOOTBALL, SportEventStatus.INACTIVE,
                LocalDateTime.of(2025, 11, 16, 15, 30, 45));
        sportEvent.setId(1L);
        sportEvent.setEventStatus(currentStatus);

        when(sportEventRepository.findById(1L)).thenReturn(Optional.of(sportEvent));

        // Act
        var exception = assertThrows(InvalidStatusChangeException.class,
                () -> sportEventService.changeEventStatus(1L, newStatus));

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Nested
    @DisplayName("Status change")
    class StatusChange {
        @Test
        @DisplayName("Test valid status change to ACTIVE")
        public void test_1() {
            // Arrange
            var sportEvent = new SportEvent("Test Event", SportType.FOOTBALL, SportEventStatus.INACTIVE,
                    LocalDateTime.of(2025, 11, 16, 15, 30, 45));
            sportEvent.setId(1L);
            sportEvent.setEventStatus(SportEventStatus.INACTIVE);
            sportEvent.setStartTime(LocalDateTime.now().plusHours(1));

            when(sportEventRepository.findById(1L)).thenReturn(Optional.of(sportEvent));

            // Act
            var updatedEvent = sportEventService.changeEventStatus(1L, SportEventStatus.ACTIVE);

            // Assert
            assertEquals(SportEventStatus.ACTIVE, updatedEvent.getEventStatus());
        }

        @Test
        @DisplayName("Test valid status change to FINISHED")
        public void test_2() {
            // Arrange
            var sportEvent = new SportEvent("Test Event", SportType.FOOTBALL, SportEventStatus.INACTIVE,
                    LocalDateTime.of(2025, 11, 16, 15, 30, 45));
            sportEvent.setId(1L);
            sportEvent.setEventStatus(SportEventStatus.ACTIVE);
            sportEvent.setStartTime(LocalDateTime.now().minusHours(1));

            when(sportEventRepository.findById(1L)).thenReturn(Optional.of(sportEvent));

            // Act
            var updatedEvent = sportEventService.changeEventStatus(1L, SportEventStatus.FINISHED);

            // Assert
            assertEquals(SportEventStatus.FINISHED, updatedEvent.getEventStatus());
        }

        @Test
        @DisplayName("Test invalid status change to ACTIVE when event started")
        public void test_3() {
            // Arrange
            var sportEvent = new SportEvent("Test Event", SportType.FOOTBALL,
                    SportEventStatus.INACTIVE, LocalDateTime.now().minusHours(1));

            when(sportEventRepository.findById(1L)).thenReturn(Optional.of(sportEvent));

            // Act
            var exception = assertThrows(InvalidStatusChangeException.class,
                    () -> sportEventService.changeEventStatus(1L, SportEventStatus.ACTIVE));

            // Assert
            assertEquals("Cannot activate a sports event that has already started", exception.getMessage());
        }

        @Test
        @DisplayName("Test FINISHED status change to FINISHED")
        public void test_4() {
            // Arrange
            var sportEvent = new SportEvent("Test Event", SportType.FOOTBALL, SportEventStatus.FINISHED,
                    LocalDateTime.of(2025, 11, 16, 15, 30, 45));
            sportEvent.setId(1L);
            sportEvent.setEventStatus(SportEventStatus.FINISHED);

            when(sportEventRepository.findById(1L)).thenReturn(Optional.of(sportEvent));

            // Act
            var exception = assertThrows(InvalidStatusChangeException.class,
                    () -> sportEventService.changeEventStatus(1L, SportEventStatus.FINISHED));

            // Assert
            assertEquals("Event is already FINISHED", exception.getMessage());
        }

        @Test
        @DisplayName("Test invalid status change default case")
        void testInvalidStatusChangeDefaultCase() {
            // Arrange
            var eventId = 1L;
            SportEventStatus invalidStatus = null;
            var sportEvent = new SportEvent();
            sportEvent.setId(eventId);
            sportEvent.setEventStatus(SportEventStatus.ACTIVE);

            when(sportEventRepository.findById(eventId)).thenReturn(Optional.of(sportEvent));

            // Act
            var exception = assertThrows(
                    InvalidStatusChangeException.class,
                    () -> sportEventService.changeEventStatus(eventId, invalidStatus)
            );

            // Assert
            assertEquals("Invalid status change from 'ACTIVE' to 'null'", exception.getMessage());
            verify(sportEventRepository).findById(eventId);
            verify(sportEventRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Sport Events")
    class GetSportEvents {

        @Test
        @DisplayName("When getting sport events with valid type and status then return sport events")
        void test_1() {
            // Arrange
            var type = SportType.FOOTBALL;
            var status = SportEventStatus.ACTIVE;
            var events = List.of(new SportEvent());

            when(sportEventRepository.findAllBySportTypeAndEventStatus(type, status)).thenReturn(events);

            // Act
            var result = sportEventService.getSportEvents(type, status);

            // Assert
            verify(sportEventRepository).findAllBySportTypeAndEventStatus(type, status);
            assertEquals(events.size(), result.size());
        }

        @Test
        @DisplayName("When getting sport events with only valid type then return sport events")
        void test_2() {
            // Arrange
            var type = SportType.BASKETBALL;
            var events = List.of(new SportEvent());

            when(sportEventRepository.findAllBySportType(type)).thenReturn(events);

            // Act
            var result = sportEventService.getSportEvents(type, null);

            // Assert
            verify(sportEventRepository).findAllBySportType(type);
            assertEquals(events.size(), result.size());
        }

        @Test
        @DisplayName("When getting sport events with only valid status then return sport events")
        void test_3() {
            // Arrange
            var status = SportEventStatus.INACTIVE;
            var events = List.of(new SportEvent());

            when(sportEventRepository.findAllByEventStatus(status)).thenReturn(events);

            // Act
            var result = sportEventService.getSportEvents(null, status);

            // Assert
            verify(sportEventRepository).findAllByEventStatus(status);
            assertEquals(events.size(), result.size());
        }

        @Test
        @DisplayName("When getting sport event with no filers then return all sport events")
        void test_4() {
            // Arrange
            var events = List.of(new SportEvent());

            when(sportEventRepository.findAll()).thenReturn(events);

            // Act
            var result = sportEventService.getSportEvents(null, null);

            // Assert
            verify(sportEventRepository).findAll();
            assertEquals(events.size(), result.size());
        }

        @Test
        @DisplayName("When getting sport event by valid id then return sport event")
        void test_5() {
            // Arrange
            var id = 1L;
            var event = new SportEvent();
            event.setId(id);

            when(sportEventRepository.findById(id)).thenReturn(Optional.of(event));

            // Act
            var result = sportEventService.getSportEventById(id);

            // Assert
            verify(sportEventRepository).findById(id);
            assertNotNull(result);
        }

        @Test
        @DisplayName("When getting sport event by invalid id then exception is thrown")
        void test_6() {
            // Arrange
            var id = 999L;

            when(sportEventRepository.findById(id)).thenReturn(Optional.empty());

            // Act
            var exception = assertThrows(
                    NoRecordFoundException.class,
                    () -> sportEventService.getSportEventById(id)
            );

            // Assert
            verify(sportEventRepository).findById(id);
            assertEquals("No Sport Event was found by id with value '999'", exception.getMessage());
        }
    }
}