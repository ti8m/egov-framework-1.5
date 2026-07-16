package ch.ti8m.egov.framework.validation.command.handler;

import ch.ti8m.egov.testbase.executor.CommandHandlerTestContextBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test-sqlserver")
class SQLServerPrimaryRepositoryResolverComponentTest extends PrimaryRepositoryResolverComponentTest {
}

@ActiveProfiles("test-postgres")
class PostgresPrimaryRepositoryResolverComponentTest extends PrimaryRepositoryResolverComponentTest {
}

abstract class PrimaryRepositoryResolverComponentTest extends CommandHandlerTestContextBase {

    @Autowired
    private CommandHandlerTestContextBase.TestExecutor testExecutor;
    @Autowired
    private PrimaryRepositoryResolverComponent primaryRepositoryResolverComponent;

    @Test
    void correctTestSetup() {
        Assertions.assertThat(testExecutor.execute(null))
                .isEmpty();
    }

    @Test
    void resolvePrimaryRepositoryThroughSpringProxy() {
        Assertions.assertThat(primaryRepositoryResolverComponent.isPrimaryRepositoryPresent(testExecutor))
                .isTrue();
    }

}
