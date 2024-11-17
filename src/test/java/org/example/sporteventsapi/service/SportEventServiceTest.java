package org.example.sporteventsapi.service;

import lombok.SneakyThrows;
import org.example.sporteventsapi.dto.SportEventDTO;
import org.example.sporteventsapi.exception.InvalidStatusChangeException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
            assertEquals(2L, result.id());
            assertEquals("Test Event", result.name());
            assertEquals(SportType.FOOTBALL, result.sportType());
            assertEquals(SportEventStatus.ACTIVE, result.eventStatus());
            verify(sportEventRepository).save(any(SportEvent.class));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "INACTIVE, FINISHED, 'Cannot change status from INACTIVE to FINISHED'",
            "ACTIVE, ACTIVE, 'Event is already ACTIVE'",
            "FINISHED, INACTIVE, 'Cannot change status from FINISHED to INACTIVE'",
            "FINISHED, ACTIVE, 'Cannot change status from FINISHED to ACTIVE'"
    })
    public void testInvalidStatusChange(SportEventStatus currentStatus, SportEventStatus newStatus, String expectedMessage) {
        // Arrange
        var sportEvent = new SportEvent("Test Event", SportType.FOOTBALL, SportEventStatus.INACTIVE,
                LocalDateTime.of(2025, 11, 16, 15, 30, 45));
        sportEvent.setId(1L);
        sportEvent.setEventStatus(currentStatus);
        when(sportEventRepository.findById(1L)).thenReturn(Optional.of(sportEvent));

        // Act
        InvalidStatusChangeException exception = assertThrows(InvalidStatusChangeException.class,
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
            SportEventDTO updatedEvent = sportEventService.changeEventStatus(1L, SportEventStatus.ACTIVE);

            // Assert
            assertEquals(SportEventStatus.ACTIVE, updatedEvent.eventStatus());
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
            SportEventDTO updatedEvent = sportEventService.changeEventStatus(1L, SportEventStatus.FINISHED);

            // Assert
            assertEquals(SportEventStatus.FINISHED, updatedEvent.eventStatus());
        }

        @Test
        @DisplayName("Test invalid status change to active when event started")
        public void test_3() {
            // Arrange
            var sportEvent = new SportEvent("Test Event", SportType.FOOTBALL,
                    SportEventStatus.INACTIVE, LocalDateTime.now().minusHours(1));
            when(sportEventRepository.findById(1L)).thenReturn(Optional.of(sportEvent));

            // Act
            InvalidStatusChangeException exception = assertThrows(InvalidStatusChangeException.class,
                    () -> sportEventService.changeEventStatus(1L, SportEventStatus.ACTIVE));

            // Assert
            assertEquals("Cannot activate a sports event that has already started", exception.getMessage());
        }

        @Test
        @DisplayName("Test finished status change to finished")
        public void test_4() {
            // Arrange
            var sportEvent = new SportEvent("Test Event", SportType.FOOTBALL, SportEventStatus.FINISHED,
                    LocalDateTime.of(2025, 11, 16, 15, 30, 45));
            sportEvent.setId(1L);
            sportEvent.setEventStatus(SportEventStatus.FINISHED);
            when(sportEventRepository.findById(1L)).thenReturn(Optional.of(sportEvent));

            // Act
            InvalidStatusChangeException exception = assertThrows(InvalidStatusChangeException.class,
                    () -> sportEventService.changeEventStatus(1L, SportEventStatus.FINISHED));

            // Assert
            assertEquals("Event is already FINISHED", exception.getMessage());

        }
    }
}