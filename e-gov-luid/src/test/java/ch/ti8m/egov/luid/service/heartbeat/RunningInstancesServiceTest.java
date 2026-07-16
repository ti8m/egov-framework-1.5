package ch.ti8m.egov.luid.service.heartbeat;

import ch.ti8m.egov.luid.deployconfig.SchedulingConfig;
import ch.ti8m.egov.luid.entity.RunningInstance;
import ch.ti8m.egov.luid.repository.RunningInstancesRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ImportAutoConfiguration(exclude = SchedulingConfig.class)
class RunningInstancesServiceTest {

    @Autowired
    RunningInstancesService runningInstancesService;
    @MockBean
    InstanceLifecycleComponent instanceLifecycleComponent;
    @SpyBean
    RunningInstancesRepository runningInstancesRepository;
    @MockBean
    Clock clock;

    @Test
    void noDoubleRegistration() {
        runningInstancesService.registerInstance(InstanceLifecycleComponent.getInstanceId());
        runningInstancesService.registerInstance(InstanceLifecycleComponent.getInstanceId());
        runningInstancesService.registerInstance(InstanceLifecycleComponent.getInstanceId());

        Mockito.verify(runningInstancesRepository, Mockito.times(1))
                .save(Mockito.any(RunningInstance.class));
    }

    @Test
    void instancesWithOutdatedHeartbeatDeleted() {
        final Instant fixedInstant = Instant.parse("2024-04-29T15:00:00Z");
        Mockito.when(clock.instant()).thenReturn(fixedInstant);
        Mockito.when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        final RunningInstance runningInstance = runningInstancesRepository.save(RunningInstance.builder()
                .heartbeat(fixedInstant.minus(10, ChronoUnit.MINUTES).toEpochMilli())
                .build());
        Assertions.assertThat(runningInstance).isNotNull();
        Assertions.assertThat(runningInstancesRepository.findById(runningInstance.getId())).isPresent();

        runningInstancesService.cleanupStaleInstances(5);
        Assertions.assertThat(runningInstancesRepository.findById(runningInstance.getId())).isNotPresent();
    }

    @Test
    void instancesWithCurrentHeartbeatKeptAlive() {
        final Instant fixedInstant = Instant.parse("2024-04-29T15:00:00Z");
        Mockito.when(clock.instant()).thenReturn(fixedInstant);
        Mockito.when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        final RunningInstance runningInstance = runningInstancesRepository.save(RunningInstance.builder()
                .heartbeat(fixedInstant.minus(2, ChronoUnit.MINUTES).toEpochMilli())
                .build());
        Assertions.assertThat(runningInstance).isNotNull();
        Assertions.assertThat(runningInstancesRepository.findById(runningInstance.getId())).isPresent();

        runningInstancesService.cleanupStaleInstances(5);
        Assertions.assertThat(runningInstancesRepository.findById(runningInstance.getId())).isPresent();
    }

    @Test
    void instancesWithNullHeartbeatKeptAlive() {
        final Instant fixedInstant = Instant.parse("2024-04-29T15:00:00Z");
        Mockito.when(clock.instant()).thenReturn(fixedInstant);
        Mockito.when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        final RunningInstance runningInstance = runningInstancesRepository.save(RunningInstance.builder()
                .heartbeat(null)
                .build());
        Assertions.assertThat(runningInstance).isNotNull();
        Assertions.assertThat(runningInstancesRepository.findById(runningInstance.getId())).isPresent();

        runningInstancesService.cleanupStaleInstances(5);
        Assertions.assertThat(runningInstancesRepository.findById(runningInstance.getId())).isPresent();
    }

    @Test
    void updateCurrentInstanceHeartBeat() {
        final Instant fixedInstant = Instant.parse("2024-04-29T15:00:00Z");
        Mockito.when(clock.instant()).thenReturn(fixedInstant);
        Mockito.when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        final RunningInstance runningInstance = runningInstancesRepository.save(RunningInstance.builder()
                .instanceId(UUID.randomUUID())
                .segmentId(3)
                .heartbeat(fixedInstant.minus(2, ChronoUnit.MINUTES).toEpochMilli())
                .build());
        runningInstancesService.updateInstance(runningInstance.getInstanceId(), runningInstance.getSegmentId());

        Assertions.assertWith(runningInstancesRepository.findById(runningInstance.getId()), retrievedRunningInstance -> {
            Assertions.assertThat(retrievedRunningInstance).isPresent();
            Assertions.assertThat(retrievedRunningInstance.get().getHeartbeat()).isEqualTo(fixedInstant.toEpochMilli());
        });
    }

}