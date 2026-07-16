package ch.ti8m.egov.framework.persistence.base;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.testbase.TestApplicationContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GlobalRepositoryConfigurationServiceTest extends TestApplicationContext {

    private GlobalRepositoryConfigurationService globalRepositoryConfigurationService;

    @BeforeEach
    public void setUp() {
        globalRepositoryConfigurationService = new GlobalRepositoryConfigurationService();
    }

    @Test
    void permissionsDeactivated() {
        globalRepositoryConfigurationService.deactivateRepositoryPermissions(TestRepository.class);
        Assertions.assertThat(globalRepositoryConfigurationService.isDeactivated(TestRepository.class)).isTrue();
    }

    @Test
    void permissionsActivatedByDefault() {
        Assertions.assertThat(globalRepositoryConfigurationService.isDeactivated(TestRepository.class)).isFalse();
    }

    @Test
    void permissionsActivatedCorrectly() {
        globalRepositoryConfigurationService.deactivateRepositoryPermissions(TestRepository.class);
        globalRepositoryConfigurationService.activateRepositoryPermissions(TestRepository.class);
        Assertions.assertThat(globalRepositoryConfigurationService.isDeactivated(TestRepository.class)).isFalse();
    }

    @Test
    void activationThrowsNoExceptionIfAlreadyActivated() {
        globalRepositoryConfigurationService.activateRepositoryPermissions(TestRepository.class);
        Assertions.assertThat(globalRepositoryConfigurationService.isDeactivated(TestRepository.class)).isFalse();
    }

    private static class TestRepository extends BaseRepositoryImpl<Command> {

    }

}