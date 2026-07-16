package ch.ti8m.egov.luid.service;


import ch.ti8m.egov.luid.deployconfig.LuidConfig;
import ch.ti8m.egov.luid.deployconfig.SchedulingConfig;
import ch.ti8m.egov.luid.service.heartbeat.InstanceLifecycleComponent;
import org.assertj.core.api.Assertions;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest(properties = {
        "egov.persistence.luid.start-year=2025",
        "egov.persistence.luid.max-ids-per-tick=5"
})
@ImportAutoConfiguration(exclude = SchedulingConfig.class)
public class LUIDGeneratorTest {

    @MockBean
    Clock clock;
    @MockBean
    InstanceLifecycleComponent instanceLifecycleComponent;
    @Autowired
    LUIDGenerator luidGenerator;
    @Autowired
    LuidConfig luidConfig;
    @Mock
    private SharedSessionContractImplementor session;

    @AfterEach
    void tearDown() {
        LUIDGenerator.incFromBase = 0;
        LUIDGenerator.lastMillis = -1;
    }

    @Test
    public void givenSegmentIdSet_whenGenerate_thenReturnGeneratedValue() {
        final Instant fixedInstant = Instant.parse("2025-01-01T00:00:00Z");
        Mockito.when(clock.instant()).thenReturn(fixedInstant);
        Mockito.when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        luidConfig.setSegmentId(0);

        final long generatedId = luidGenerator.generate(session, new Object());
        Assertions.assertThat(generatedId).isEqualTo(0);
    }

    @Test
    public void segment1returnsFirstIdOfSegment1() {
        final Instant fixedInstant = Instant.parse("2025-01-01T00:00:00Z");
        Mockito.when(clock.instant()).thenReturn(fixedInstant);
        Mockito.when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        luidConfig.setSegmentId(1);

        final long generatedId = luidGenerator.generate(session, new Object());
        Assertions.assertThat(generatedId).isEqualTo(5);
    }

    @Test
    public void generatorReturnsIncrementingIdsWithinSameStep() {
        final Instant fixedInstant = Instant.parse("2025-01-01T00:00:00Z");
        Mockito.when(clock.instant()).thenReturn(fixedInstant);
        Mockito.when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        luidConfig.setSegmentId(0);


        final List<Long> ids = IntStream.range(0, 5)
                .mapToObj(i -> luidGenerator.generate(session, new Object()))
                .toList();

        IntStream.range(1, ids.size())
                .forEach(i -> Assertions.assertThat(ids.get(i) > ids.get(i - 1)).isTrue());
    }
}