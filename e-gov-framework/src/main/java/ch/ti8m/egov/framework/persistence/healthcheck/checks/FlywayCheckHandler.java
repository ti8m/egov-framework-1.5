package ch.ti8m.egov.framework.persistence.healthcheck.checks;

import ch.ti8m.egov.framework.exceptionhandling.utils.ExceptionUtils;
import ch.ti8m.egov.framework.persistence.healthcheck.model.FlywayMigration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FlywayCheckHandler implements HealthCheck {

    private static final int NUMBER_OF_FLYWAY_MIGRATIONS = 300;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> runHealthCheck() {
        try {
            final List<FlywayMigration> flywayMigrations = (List<FlywayMigration>) entityManager
                    .createNativeQuery(
                            "SELECT TOP(" + NUMBER_OF_FLYWAY_MIGRATIONS + ") * FROM flyway_schema_history ORDER BY installed_rank DESC",
                            FlywayMigration.class
                    )
                    .getResultList();
            return Map.of(
                    STATUS, SUCCESS,
                    MIGRATIONS, flywayMigrations
            );
        } catch (Exception e) {
            return Map.of(
                    STATUS, ERROR,
                    MESSAGE, e.getMessage() == null ? "" : e.getMessage(),
                    STACK_TRACE, e.getStackTrace() == null ? "" : ExceptionUtils.getStackTrace(e)
            );
        }
    }

}
