package ch.ti8m.egov.framework.validation.command.subscription;

import ch.ti8m.egov.framework.validation.command.Command;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Slf4j
public class SubscriptionDistributionService {

    private final TaskExecutor taskExecutor;
    private final SubscriptionExecutionService subscriptionExecutionService;

    public SubscriptionDistributionService(
            @Qualifier(SubscriptionConfiguration.SUBSCRIPTION_TASK_EXECUTOR_BEAN_NAME) final TaskExecutor taskExecutor,
            final SubscriptionExecutionService subscriptionExecutionService
    ) {
        this.taskExecutor = taskExecutor;
        this.subscriptionExecutionService = subscriptionExecutionService;
    }

    @Async
    @EventListener(SubscriptionEvent.class)
    public void routeSubscriptionEvent(final SubscriptionEvent subscriptionEvent) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                // Transaction active, delay event
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        handleSubscriptionEvent(subscriptionEvent);
                    }
                });
            } else {
                // Synchronization not active, fallback to immediate execution
                handleSubscriptionEvent(subscriptionEvent);
            }
        } else {
            // No transaction – execute immediately
            handleSubscriptionEvent(subscriptionEvent);
        }
    }

    private void handleSubscriptionEvent(final SubscriptionEvent subscriptionEvent) {
        try {
            taskExecutor.execute(() ->
                    subscriptionExecutionService.execute(
                            subscriptionEvent.getSubscriber(),
                            subscriptionEvent.getCommand(),
                            subscriptionEvent.getUserId()
                    )
            );
        } catch (final Exception e) {
            SubscriptionDistributionService.log.error("Subscriber execution failed for {}: {}", subscriptionEvent.getSubscriber().getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    @Getter
    public static class SubscriptionEvent extends ApplicationEvent {
        private final String userId;
        private final Command command;
        private final Subscriber subscriber;

        public SubscriptionEvent(
                final Object source,
                final String userId,
                final Command command, final Subscriber subscriber
        ) {
            super(source);
            this.userId = userId;
            this.command = command;
            this.subscriber = subscriber;
        }
    }
}
