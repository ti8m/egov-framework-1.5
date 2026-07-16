package ch.ti8m.egov.framework.persistence.healthcheck.checks;


import ch.ti8m.egov.framework.exceptionhandling.utils.ExceptionUtils;
import ch.ti8m.egov.framework.persistence.healthcheck.model.RowCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RowCountsHandler implements HealthCheck {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> runHealthCheck() {
        try {
            final List<RowCount> rowCounts = (List<RowCount>) entityManager
                    .createNativeQuery(
                            "SELECT * FROM EGOV_PER_AA_RowCount",
                            RowCount.class
                    )
                    .getResultList();
            return Map.of(
                    STATUS, SUCCESS,
                    ENTRIES, rowCounts
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
