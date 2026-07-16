package ch.ti8m.egov.framework.validation.command.subscription;

import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.engine.ValidationEngine;
import ch.ti8m.egov.testbase.commandhandling.CommandExecutionTestContextBase;
import ch.ti8m.egov.testbase.repositories.TestEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test-postgres")
class SubscriptionServiceIT extends CommandExecutionTestContextBase {

    @Autowired
    TestApplicationService testApplicationService;
    @Autowired
    TestEntityRepository testEntityRepository;
    @Autowired
    PersistingTestExecutor testExecutor;

    @MockBean
    ValidationEngine validationEngine;

    @BeforeEach
    void setUp() {
        testEntityRepository.deactivatePermissions();
    }

    @Test
    void correctSetupPersistsNewEntity() {
        final Long id = (Long) testApplicationService.handleCommand(Command.builder()
                .action(PersistingTestExecutorSubscriber.SUBSCRIBED_COMMAND)
                .parameters(Parameters.builder().build())
                .executionPlan(Command.ExecutionPlan.DONT_VALIDATE)
                .executor(testExecutor)
                .build());

        Assertions.assertWith(testEntityRepository.findById(id), testEntity -> {
            Assertions.assertThat(testEntity).isNotNull();
        });
    }

}