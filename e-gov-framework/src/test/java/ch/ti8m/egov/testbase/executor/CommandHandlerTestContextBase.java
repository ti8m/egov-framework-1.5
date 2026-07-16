package ch.ti8m.egov.testbase.executor;

import ch.ti8m.egov.TxTest;
import ch.ti8m.egov.framework.persistence.base.PrimaryRepository;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.framework.validation.command.NonTxExecutor;
import ch.ti8m.egov.testbase.entities.relationship.TestEntity;
import ch.ti8m.egov.testbase.repositories.TestEntityRepository;
import ch.ti8m.egov.testbase.tools.RepositoryTestBase;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@ContextConfiguration(classes = {
        CommandHandlerTestContextBase.TestExecutor.class,
        CommandHandlerTestContextBase.TestExecutorDeclaredNonTransactional.class,
        CommandHandlerTestContextBase.SubComponent.class
})
public class CommandHandlerTestContextBase extends RepositoryTestBase {

    @Component
    @RequiredArgsConstructor
    public static class TestExecutor implements Executor {

        @PrimaryRepository
        private final TestEntityRepository testEntityRepository;

        @Override
        public List<TestEntity> execute(final Command command) {
            if (command != null && command.getCommandValue() instanceof final TxTest.TransactionStateDto transactionStateDto) {
                transactionStateDto.setTransactionActive(TransactionSynchronizationManager.isActualTransactionActive());
            }
            testEntityRepository.deactivatePermissions();
            return testEntityRepository.findAll();
        }
    }

    @Component
    @RequiredArgsConstructor
    public static class TestExecutorDeclaredNonTransactional implements Executor {

        @PrimaryRepository
        private final TestEntityRepository testEntityRepository;
        private final SubComponent subComponent;

        @Override
        @Transactional(propagation = Propagation.NOT_SUPPORTED)
        public List<TestEntity> execute(final Command command) {
            if (command != null && command.getCommandValue() instanceof final TxTest.TransactionStateDto transactionStateDto) {
                transactionStateDto.setTransactionActive(TransactionSynchronizationManager.isActualTransactionActive());
            }

            subComponent.runWithinTx();

            testEntityRepository.deactivatePermissions();
            return testEntityRepository.findAll();
        }
    }

    @Component
    @RequiredArgsConstructor
    public static class TestExecutorNonTransactional implements NonTxExecutor {

        @PrimaryRepository
        private final TestEntityRepository testEntityRepository;
        private final SubComponent subComponent;

        @Override
        public List<TestEntity> execute(final Command command) {
            if (command != null && command.getCommandValue() instanceof final TxTest.TransactionStateDto transactionStateDto) {
                transactionStateDto.setTransactionActive(TransactionSynchronizationManager.isActualTransactionActive());
            }

            subComponent.runWithinTx();

            testEntityRepository.deactivatePermissions();
            return testEntityRepository.findAll();
        }
    }

    @Component
    public static class SubComponent {

        @Transactional
        public void runWithinTx() {
            Assertions.assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isTrue();
        }

    }

}
