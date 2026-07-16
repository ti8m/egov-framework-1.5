package ch.ti8m.egov;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.executor.BaseExecutor;
import ch.ti8m.egov.testbase.executor.CommandHandlerTestContextBase;
import lombok.Data;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test-sqlserver")
class SQLServerTxTest extends TxTest {
}

@ActiveProfiles("test-postgres")
class PostgresTxTest extends TxTest {
}

public abstract class TxTest extends CommandHandlerTestContextBase implements ApplicationContextAware {

    protected static ApplicationContext applicationContext;
    protected BaseExecutor transactionalExecutor;
    protected BaseExecutor declaredNonTransactionalExecutor;
    protected BaseExecutor nonTransactionalExecutor;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        TxTest.applicationContext = applicationContext;
    }

    @BeforeEach
    void setUp() {
        DataHolder.cleanUp();
        transactionalExecutor = TxTest.applicationContext.getBean(TestExecutor.class);
        declaredNonTransactionalExecutor = TxTest.applicationContext.getBean(TestExecutorDeclaredNonTransactional.class);
        nonTransactionalExecutor = TxTest.applicationContext.getBean(TestExecutorNonTransactional.class);
    }

    @Test
    void contextLoads() {
        Assertions.assertThat(transactionalExecutor).isNotNull();
        Assertions.assertThat(declaredNonTransactionalExecutor).isNotNull();
        Assertions.assertThat(nonTransactionalExecutor).isNotNull();
    }

    @Test
    void testTransactionalExecutor() {
        final TransactionStateDto transactionStateDto = new TransactionStateDto();
        transactionalExecutor.execute(Command.builder()
                .commandValue(transactionStateDto)
                .build());

        Assertions.assertThat(transactionStateDto.isTransactionActive()).isTrue();
    }

    @Test
    void testDeclaredNonTransactionalExecutor() {
        final TransactionStateDto transactionStateDto = new TransactionStateDto();
        declaredNonTransactionalExecutor.execute(Command.builder()
                .commandValue(transactionStateDto)
                .build());

        Assertions.assertThat(transactionStateDto.isTransactionActive()).isFalse();
    }

    @Test
    void testNonTransactionalExecutor() {
        final TransactionStateDto transactionStateDto = new TransactionStateDto();
        nonTransactionalExecutor.execute(Command.builder()
                .commandValue(transactionStateDto)
                .build());

        Assertions.assertThat(transactionStateDto.isTransactionActive()).isFalse();
    }

    @Data
    public static class TransactionStateDto {
        private boolean isTransactionActive;
    }

}
