package org.example.sporteventsapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = {Application.class})
class ApplicationTest {

    @Test
    @DisplayName("Application throw no exception on startup")
    void testApplicationStartup() {
        assertThat(true, is(true));
    }
}
