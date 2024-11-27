package org.example.sporteventsapi.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SportEventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("When creating a sport event with ID, validation fails")
    void testCreateSportEventWithIdValidation() throws Exception {
        var invalidPayload = """
                {
                    "id": 5,
                    "name": "",
                    "sportType": null,
                    "eventStatus": null,
                    "startTime": null
                }
                """;

        mockMvc.perform(post("/sport-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'id')].message")
                        .value("ID must be null when creating a new sport event"))
                .andExpect(jsonPath("$.errors[?(@.field == 'name')].message")
                        .value("Name must not be blank"))
                .andExpect(jsonPath("$.errors[?(@.field == 'startTime')].message")
                        .value("StartTime must not be null"));
    }
}
