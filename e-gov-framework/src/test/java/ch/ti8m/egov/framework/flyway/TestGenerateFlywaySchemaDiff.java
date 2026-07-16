package ch.ti8m.egov.framework.flyway;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("flyway-generate")
@Disabled
class TestGenerateFlywaySchemaDiff extends TestFlywayContextBase {
    @Test
    void givenFlywayInit_hibernateValidation_createSchemaDiff() {
        log.info("schema generation successful");
    }
}
