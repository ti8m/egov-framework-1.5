package ch.ti8m.egov.demo.shared;

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
                .locations("classpath:egov/rulesets")
                .dataSource(dataSource)
                .load()
                .migrate();
    }

}