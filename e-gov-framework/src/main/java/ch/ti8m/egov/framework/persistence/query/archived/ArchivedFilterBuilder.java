package ch.ti8m.egov.framework.persistence.query.archived;


import ch.ti8m.egov.framework.persistence.base.ArchivedModifiableEntity;
import ch.ti8m.egov.framework.persistence.query.ParametrizedQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArchivedFilterBuilder {

    public ParametrizedQuery getArchivedFilter(final String baseQuery) {
        return new ParametrizedQuery(
                baseQuery + " WHERE " + ArchivedModifiableEntity.ARCHIVED_FIELD + " = ?",
                List.of(Boolean.TRUE)
        );
    }

}
