package ch.ti8m.egov.framework.persistence.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ParametrizedQuery {
    private final String query;
    private final List<Object> parameters;

    public ParametrizedQuery(final String query) {
        this.query = query;
        this.parameters = new ArrayList<>();
    }

    public ParametrizedQuery(final ParametrizedQuery parametrizedQuery) {
        this.query = parametrizedQuery.query;
        this.parameters = new ArrayList<>(parametrizedQuery.parameters);
    }
}