package ch.ti8m.egov.framework.validation.command;

import ch.ti8m.egov.testbase.TestApplicationContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "egov.persistence.database.command.value-length=3"
})
class ObjectConverterIT extends TestApplicationContext {

    ObjectConverter objectConverter = new ObjectConverter();

    @Test
    void setCommandValueLength() {
        final String result = objectConverter.convertToDatabaseColumn("test");

        Assertions.assertThat(result).isEqualTo("\"te");
    }
}