package ch.ti8m.egov.framework.persistence.query;

import org.springframework.stereotype.Component;

@Component
public class CountQueryBuilder {

    public String buildCountQuery(final String query) {
        return "SELECT COUNT(*) FROM (" + query + ") AS IdCountTable";
    }

}
