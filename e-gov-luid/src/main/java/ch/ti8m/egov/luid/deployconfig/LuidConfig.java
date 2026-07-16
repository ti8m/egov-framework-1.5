package ch.ti8m.egov.luid.deployconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
public class LuidConfig {

    @Value("${egov.persistence.luid.max-ids-per-tick:10000}")
    private long maxIDsPerTick;

    @Value("${egov.persistence.luid.max-segments:100}")
    private int maxSegments; // = count of pods or jvms in parallel

    @Value("${egov.persistence.luid.start-year:2025}")
    private int startYear;

    private int segmentId = -1;

}
