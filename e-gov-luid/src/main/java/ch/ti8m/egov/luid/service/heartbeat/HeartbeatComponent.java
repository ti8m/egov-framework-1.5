package ch.ti8m.egov.luid.service.heartbeat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class HeartbeatComponent {

    private final RunningInstancesService runningInstancesService;
    private final InstanceLifecycleComponent instanceLifecycleComponent;

    @Scheduled(fixedDelayString = "${egov.persistence.luid.heartbeat.delay:1000}")
    public void sendHeartbeat() {
        final long tick = runningInstancesService.updateInstance(InstanceLifecycleComponent.getInstanceId(), instanceLifecycleComponent.getSegmentId());
        log.debug(
                """
                        Heartbeat sent at: {}
                        From Instance: {}
                        SegmentId: {}
                        """,
                tick,
                InstanceLifecycleComponent.getInstanceId(),
                instanceLifecycleComponent.getSegmentId()
        );
    }
}
