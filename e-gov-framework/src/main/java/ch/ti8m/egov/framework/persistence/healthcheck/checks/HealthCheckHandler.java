package ch.ti8m.egov.framework.persistence.healthcheck.checks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HealthCheckHandler implements HealthCheck {

    private final FlywayCheckHandler flywayCheckHandler;

    private final OperationCheckHandler operationCheckHandler;

    private final StoredProcedureCheckHandler storedProcedureCheckHandler;

    private final PersistenceInfoHandler persistenceInfoHandler;

    private final RowCountsHandler rowCountsHandler;

    @Autowired
    public HealthCheckHandler(FlywayCheckHandler flywayCheckHandler, OperationCheckHandler operationCheckHandler, StoredProcedureCheckHandler storedProcedureCheckHandler, PersistenceInfoHandler persistenceInfoHandler, RowCountsHandler rowCountsHandler) {
        this.flywayCheckHandler = flywayCheckHandler;
        this.operationCheckHandler = operationCheckHandler;
        this.storedProcedureCheckHandler = storedProcedureCheckHandler;
        this.persistenceInfoHandler = persistenceInfoHandler;
        this.rowCountsHandler = rowCountsHandler;
    }

    public Map<String, Object> runHealthCheck() {
        return Map.of(
                HEALTH_FLYWAY, flywayCheckHandler.runHealthCheck(),
                HEALTH_OPERATIONS, operationCheckHandler.runHealthCheck(),
                HEALTH_STORED_PROCEDURES, storedProcedureCheckHandler.runHealthCheck(),
                PERSISTENCE_INFO, persistenceInfoHandler.runHealthCheck(),
                ROW_COUNT, rowCountsHandler.runHealthCheck()
        );
    }

}
