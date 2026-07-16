package ch.ti8m.egov.luid.service.heartbeat;

import ch.ti8m.egov.luid.deployconfig.LuidConfig;
import ch.ti8m.egov.luid.entity.RunningInstance;
import ch.ti8m.egov.luid.repository.RunningInstancesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RunningInstancesService {

    private final RunningInstancesRepository runningInstancesRepository;
    private final LuidConfig luidConfig;
    private final Clock clock;

    @Transactional
    public void registerInstance(final UUID instanceId) {
        if (runningInstancesRepository.findByInstanceId(instanceId).isEmpty()) {
            runningInstancesRepository.save(RunningInstance.builder()
                    .instanceId(instanceId)
                    .build());
        }
    }

    @Transactional
    public void cleanupStaleInstances(final int minutes) {
        final long fiveMinutesAgo = Instant.now(clock).minus(Duration.ofMinutes(minutes)).toEpochMilli();
        runningInstancesRepository.findAll().stream()
                .filter(instance -> instance.getHeartbeat() != null && instance.getHeartbeat() < fiveMinutesAgo)
                .forEach(runningInstancesRepository::delete);
    }

    @Transactional
    public long updateInstance(final UUID instanceId, final Integer segmentId) {
        final long tick = Instant.now(clock).toEpochMilli();
        runningInstancesRepository.findByInstanceId(instanceId).ifPresent(instance -> {
            instance.setHeartbeat(tick);
            instance.setSegmentId(segmentId);
            runningInstancesRepository.save(instance);
        });
        return tick;
    }

    @Transactional
    public void unregisterInstance(final UUID instanceId) {
        runningInstancesRepository.findByInstanceId(instanceId).ifPresent(runningInstancesRepository::delete);
    }

    @Transactional
    public int getInstanceNumber() {
        final Set<Integer> activeSegments = runningInstancesRepository.findAll()
                .stream()
                .map(RunningInstance::getSegmentId)
                .collect(Collectors.toSet());

        return IntStream.range(0, luidConfig.getMaxSegments())
                .filter(potentialSegmentId -> !activeSegments.contains(potentialSegmentId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("All available segments are active. Cannot retrieve a new one."));
    }

}