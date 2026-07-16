package ch.ti8m.egov.mdm.api.shared;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
public class FlywayConfiguration {

    private final DataSource dataSource;

    @PostConstruct
    public void init() {

        Flyway.configure()
                .baselineOnMigrate(true)
                .locations("classpath:db/egov/postgres", "classpath:db/egov/mdm")
                .dataSource(dataSource)
                .load()
                .migrate();
    }

}