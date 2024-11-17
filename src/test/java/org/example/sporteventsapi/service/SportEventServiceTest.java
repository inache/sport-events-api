package org.example.sporteventsapi.service;

import lombok.SneakyThrows;
import org.example.sporteventsapi.dto.SportEventDTO;
import org.example.sporteventsapi.model.SportEvent;
import org.example.sporteventsapi.model.SportEventStatus;
import org.example.sporteventsapi.model.SportType;
import org.example.sporteventsapi.repository.SportEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

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
        void test_1(){
            // Arrange
            var sportEventDTO = new SportEventDTO(null, "test", SportType.FOOTBALL, SportEventStatus.ACTIVE, LocalDateTime.of(2024, 11, 16, 15, 30, 45));
            var sportEventEntity = new SportEvent("test", SportType.FOOTBALL, SportEventStatus.ACTIVE, LocalDateTime.of(2024, 11, 16, 15, 30, 45));
            sportEventEntity.setId(1L);

            when(sportEventRepository.save(any(SportEvent.class))).thenReturn(sportEventEntity);
            // Act
            var result = sportEventService.createSportEvent(sportEventDTO);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("test", result.name());
            assertEquals(SportType.FOOTBALL, result.sportType());
            assertEquals(SportEventStatus.ACTIVE, result.eventStatus());
            verify(sportEventRepository).save(any(SportEvent.class));
        }
    }
}