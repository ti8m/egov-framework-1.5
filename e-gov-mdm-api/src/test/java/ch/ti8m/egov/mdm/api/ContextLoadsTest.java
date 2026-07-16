package ch.ti8m.egov.mdm.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {ApiTestApplication.class})
@ActiveProfiles("mdm-test")
@Slf4j
class ContextLoadsTest {

    @Test
    void contextLoads() {
    }

    @AfterEach
    void tearDown() {
        log.info("Place your break point here to check data after each test");
    }
}
