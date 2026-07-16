package ch.ti8m.egov.luid.service.heartbeat;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
        "egov.persistence.luid.heartbeat.delay=100"
})
class HeartbeatComponentTest {

    @MockBean
    RunningInstancesService runningInstancesService;
    @MockBean
    InstanceLifecycleComponent instanceLifecycleComponent;

    @Test
    void testFixedOneSecondDelay() throws InterruptedException {
        Thread.sleep(350);
        Mockito.verify(runningInstancesService, Mockito.atLeast(3)).updateInstance(Mockito.any(), Mockito.any());
    }

}