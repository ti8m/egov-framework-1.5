package ch.ti8m.egov.luid.service;


import ch.ti8m.egov.luid.deployconfig.LuidConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;


/* see https://www.baeldung.com/hibernate-identifiers */
/* see https://medium.com/@sjksingh/postgresql-primary-key-dilemma-uuid-vs-bigint-52008685b744 */
/* see http://extraconversion.com/time/seconds/seconds-to-years.html */
/* sse https://www.baeldung.com/hibernate-identifiers */
/* see https://thorben-janssen.com/custom-sequence-based-idgenerator/ */

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@DependsOn("instanceLifecycleComponent")
public class LUIDGenerator implements IdentifierGenerator, Configurable {

    protected static long incFromBase = 0;
    protected static long lastMillis = -1;
    private static Instant startInstant;
    private final LuidConfig luidConfig;
    private final Clock clock;

    @PostConstruct
    public void init() {
        startInstant = LocalDate.of(luidConfig.getStartYear(), Month.JANUARY, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();
    }

    @Override
    public synchronized Long generate(
            final SharedSessionContractImplementor session,
            final Object object
    ) {
        final long tickStep = getTickStep();
        if (lastMillis != tickStep) {
            lastMillis = tickStep;
            incFromBase = luidConfig.getMaxIDsPerTick() * luidConfig.getSegmentId();
        } else {
            incFromBase += 1;
        }

        return tickStep + incFromBase;
    }

    private long getTickStep() {
        final Instant now = Instant.now(clock).truncatedTo(ChronoUnit.SECONDS);
        final long millisDifference = Duration.between(startInstant, now).toMillis();

        return millisDifference * luidConfig.getMaxIDsPerTick() * (long) luidConfig.getMaxSegments();
    }
}