package org.example.sporteventsapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = {Application.class})
class ApplicationTests {

    @Test
    void testApplicationStartup() {
        // No exceptions thrown
        assertThat(true, is(true));
    }

}
