package ch.ti8m.egov.luid.service.heartbeat;

import ch.ti8m.egov.luid.deployconfig.LuidConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InstanceLifecycleComponent {

    @Getter
    private final static UUID instanceId = UUID.randomUUID();
    private final LuidConfig luidConfig;
    private final RunningInstancesService runningInstancesService;
    @Getter
    private Integer segmentId = -1;

    @EventListener
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        runningInstancesService.cleanupStaleInstances(5);
        runningInstancesService.registerInstance(instanceId);
        segmentId = runningInstancesService.getInstanceNumber();
        luidConfig.setSegmentId(segmentId);

        log.info("""
                        Instance registered with ID: {}
                        SegmentId: {}
                        """,
                instanceId,
                segmentId
        );
    }

    @EventListener
    public void onApplicationEvent(final ContextClosedEvent event) {
        runningInstancesService.unregisterInstance(instanceId);
        log.info("Instance unregistered with ID: {}", instanceId);
    }

}