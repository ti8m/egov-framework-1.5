package ch.ti8m.egov.framework.deployconfig;

import ch.ti8m.egov.testbase.TestApplicationContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

class NoVersionIT extends VersionProviderIT {
    @Test
    void getCorrectVersion() {
        Assertions.assertThat(versionProvider.getVersion()).isEqualTo("n/a");
    }

}

@TestPropertySource(properties = {
        "egov.app.version=12ab"
})
class EnvVersionIT extends VersionProviderIT {
    @Test
    void getCorrectVersion() {
        Assertions.assertThat(versionProvider.getVersion()).isEqualTo("12ab");
    }

}

class BuildPropertyVersionIT extends VersionProviderIT {
    @MockBean
    private BuildProperties buildProperties;

    @Test
    void getCorrectVersion() {
        Mockito.when(buildProperties.getVersion()).thenReturn("34cd");
        Assertions.assertThat(versionProvider.getVersion()).isEqualTo("34cd");
    }

}

@SpringBootTest
@ActiveProfiles("test")
abstract class VersionProviderIT extends TestApplicationContext {

    @Autowired
    VersionProvider versionProvider;

    @Test
    void checkSetup() {
        Assertions.assertThat(versionProvider).isNotNull();
    }

}