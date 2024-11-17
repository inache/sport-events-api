package org.example.sporteventsapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public class AbstractJPAMockTest {
    @Inject
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
