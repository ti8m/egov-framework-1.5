package ch.ti8m.egov.framework.validation.command.subscription;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
public class SubscriptionConfiguration implements AsyncConfigurer {

    public static final String SUBSCRIPTION_TASK_EXECUTOR_BEAN_NAME = "EgovSubscriberTaskExecutor";

    @Value("${egov.subscriber.core-pool-size:5}")
    private int corePoolSize;
    @Value("${egov.subscriber.max-pool-size:20}")
    private int maxPoolSize;
    @Value("${egov.subscriber.queue-capacity:100}")
    private int queueCapacity;

    @Bean(name = SUBSCRIPTION_TASK_EXECUTOR_BEAN_NAME)
    public TaskExecutor taskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("egov-subscriber-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) ->
                log.error("Async ERROR in method: {} - {}", method.getName(), throwable.getMessage(), throwable);
    }
}
