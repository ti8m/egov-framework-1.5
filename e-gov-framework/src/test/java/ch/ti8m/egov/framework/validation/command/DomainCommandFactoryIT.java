package ch.ti8m.egov.framework.validation.command;

import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.testbase.TestApplicationContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "egov.app.version=12ab"
})
class DomainCommandFactoryIT extends TestApplicationContext {

    @Autowired
    TestDomainCommandFactory testDomainCommandFactory;

    @Test
    void checkSetup() {
        Assertions.assertThat(testDomainCommandFactory).isNotNull();
    }

    @Test
    void checkVersionSettingInCommand() {
        final Command command = testDomainCommandFactory.getCommand(
                "MY_ACTION",
                "n/a",
                null,
                Parameters.builder().build());
        Assertions.assertThat(command.getVersion()).isEqualTo("12ab");
    }

    @Component
    public static class TestDomainCommandFactory extends DomainCommandFactory {
        @Override
        protected void setExecutionDetails(final Command command) {

        }
    }

}