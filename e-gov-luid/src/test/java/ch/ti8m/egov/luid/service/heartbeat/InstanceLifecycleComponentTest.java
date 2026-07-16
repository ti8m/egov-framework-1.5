package ch.ti8m.egov.luid.service.heartbeat;

import ch.ti8m.egov.luid.deployconfig.LuidConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class InstanceLifecycleComponentTest {

    @SpyBean
    InstanceLifecycleComponent instanceLifecycleComponent;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @SpyBean
    private LuidConfig luidConfig;

    @Test
    void checkStartupRoutine() {
        Mockito.verify(instanceLifecycleComponent, Mockito.times(1))
                .onApplicationEvent(Mockito.any(ContextRefreshedEvent.class));
        Mockito.verify(luidConfig, Mockito.times(1)).setSegmentId(instanceLifecycleComponent.getSegmentId());
    }

    @Test
    void checkShutdownRoutine() {
        eventPublisher.publishEvent(new ContextClosedEvent((ConfigurableApplicationContext) eventPublisher));

        Mockito.verify(instanceLifecycleComponent, Mockito.times(1))
                .onApplicationEvent(Mockito.any(ContextClosedEvent.class));
    }

}