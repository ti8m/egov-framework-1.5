package ch.ti8m.egov.testbase.commandhandling;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.framework.validation.command.handler.CommandHandlerBase;
import ch.ti8m.egov.framework.validation.command.subscription.Subscriber;
import ch.ti8m.egov.framework.validation.command.subscription.SubscriptionService;
import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;
import ch.ti8m.egov.testbase.entities.relationship.TestEntity;
import ch.ti8m.egov.testbase.repositories.TestEntityRepository;
import ch.ti8m.egov.testbase.tools.RepositoryTestBase;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        CommandExecutionTestContextBase.PersistingTestExecutor.class,
        CommandExecutionTestContextBase.PersistingTestExecutorSubscriber.class,
        CommandExecutionTestContextBase.TestApplicationService.class
})
public class CommandExecutionTestContextBase extends RepositoryTestBase {

    @Component
    public static class TestApplicationService extends CommandHandlerBase<Object> {

        @Override
        protected void loadAggregate(final Command command) {
        }

        @Override
        protected ValidationMethodMapper provideValidationMethodMapper() {
            return null;
        }

        @SneakyThrows
        @Override
        public Object handleCommand(final Command command) {
            final Object result = super.handleCommand(command);
            Thread.sleep(5000);
            return result;
        }

    }

    @Component
    @RequiredArgsConstructor
    public static class PersistingTestExecutorSubscriber extends Subscriber {
        public static final String SUBSCRIBED_COMMAND = "SUBSCRIBED_COMMAND";
        private final SubscriptionService subscriptionService;
        private final TestEntityRepository testEntityRepository;

        @Override
        @PostConstruct
        public void doSubscribe() {
            subscriptionService.subscribe(this, SUBSCRIBED_COMMAND);
            testEntityRepository.deactivatePermissionsGlobally();
        }

        @Override
        protected void execute(final Command command) {
            Assertions.assertWith(testEntityRepository.findById((Long) command.getCommandValue()), optionalTestEntity -> {
                System.out.println("------ TestEntity: " + optionalTestEntity);
                Assertions.assertThat(optionalTestEntity).isPresent();
            });
        }
    }

    @Component
    @RequiredArgsConstructor
    public static class PersistingTestExecutor implements Executor {

        private final TestEntityRepository testEntityRepository;

        @Override
        public Long execute(final Command command) {
            final TestEntity testEntity = TestEntity.builder()
                    .firstname("Moritz")
                    .lastname("Baumotte")
                    .build();
            testEntityRepository.save(testEntity);
            return testEntity.getId();
        }
    }

}
