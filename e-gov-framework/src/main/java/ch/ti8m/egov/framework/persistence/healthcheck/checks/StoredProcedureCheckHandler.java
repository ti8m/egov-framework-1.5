package ch.ti8m.egov.framework.persistence.healthcheck.checks;


import ch.ti8m.egov.framework.exceptionhandling.utils.ExceptionUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StoredProcedureCheckHandler implements HealthCheck {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> runHealthCheck() {
        try {
            entityManager.createNativeQuery("EXECUTE dbo.EGOV_PER_AA_HealthCheckStoredProcedure")
                    .executeUpdate();
            return Map.of(STATUS, SUCCESS);
        } catch (Exception e) {
            return Map.of(
                    STATUS, ERROR,
                    MESSAGE, e.getMessage() == null ? "" : e.getMessage(),
                    STACK_TRACE, e.getStackTrace() == null ? "" : ExceptionUtils.getStackTrace(e)
            );
        }
    }
}
