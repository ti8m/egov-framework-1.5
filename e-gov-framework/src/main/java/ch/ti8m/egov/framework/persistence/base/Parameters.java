package ch.ti8m.egov.framework.persistence.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class Parameters implements Serializable {

    private String sender;
    private Long aggregateId;
    private Integer offset;
    private Integer limit;
    private String filter;
    private String sorting;
    private String userId;
    private boolean skipPagination = false;
    // Forces Jackson to (de)serialize despite the protected getParams() override below.
    @JsonProperty
    private Map<String, Object> params;

    private Parameters(
            final String sender,
            final Long aggregateId,
            final Integer offset,
            final Integer limit,
            final String filter,
            final String sorting,
            final String userId,
            final boolean skipPagination,
            final Map<String, Object> params
    ) {
        this.sender = sender;
        this.aggregateId = aggregateId;
        this.offset = offset;
        this.limit = limit;
        this.filter = filter;
        this.sorting = sorting;
        this.userId = userId;
        this.skipPagination = skipPagination;
        this.params = params == null ? new HashMap<>() : params;
    }

    public static ParametersBuilder builder() {
        return new ParametersBuilder();
    }

    @JsonIgnore
    public Set<String> getAvailableParameters() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params.keySet();
    }

    @JsonIgnore
    public String getAsQueryParams(final String... paramValues) {
        return Arrays.stream(paramValues)
                .filter(paramValue -> params.get(paramValue) != null)
                .map(paramValue -> paramValue + "=" + URLEncoder.encode((String) params.get(paramValue), StandardCharsets.UTF_8).replace("+", "%20"))
                .collect(Collectors.joining("&"));
    }

    /**
     * @return the parameters as a Map
     * @deprecated Only for saving the parameters in the CP_CMD_COMMAND table, please use {@link #getParameter(String)} or {@link #getParameterAs(String)} instead
     */
    @Deprecated
    protected Map<String, Object> getParams() {
        return params;
    }

    public Object getParameter(final String parameterName) {
        if (params == null) {
            return null;
        }
        return params.get(parameterName);
    }

    public <T> T getParameterAs(final String parameterName) {
        if (params == null) {
            return null;
        }
        return (T) params.get(parameterName);
    }

    public void appendFilter(final String filter) {
        this.filter += filter;
    }

    public void add(final String parameterName, final Object parameterValue) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(parameterName, parameterValue);
    }

    public static class ParametersBuilder {

        private String sender;
        private Long aggregateId;
        private Integer offset;
        private Integer limit;
        private String userId;
        private String filter;
        private String sorting;
        private boolean skipPagination = false;
        private Map<String, Object> params;

        ParametersBuilder() {
        }

        public ParametersBuilder sender(final String sender) {
            this.sender = sender;
            return this;
        }

        public ParametersBuilder aggregateId(final Long aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public ParametersBuilder offset(final Integer offset) {
            this.offset = offset;
            return this;
        }

        public ParametersBuilder limit(final Integer limit) {
            this.limit = limit;
            return this;
        }

        public ParametersBuilder filter(final String filter) {
            this.filter = filter;
            return this;
        }

        public ParametersBuilder sorting(final String sorting) {
            this.sorting = sorting;
            return this;
        }

        public ParametersBuilder userId(final String userId) {
            this.userId = userId;
            return this;
        }

        public ParametersBuilder skipPagination(final boolean skipPagination) {
            this.skipPagination = skipPagination;
            return this;
        }

        public ParametersBuilder addString(final String parameterName, final String value) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put(parameterName, value);
            return this;
        }

        public String getString(final String parameterName) {
            return (params == null) ? null : params.get(parameterName).toString();
        }

        public ParametersBuilder add(final String parameterName, final Object value) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put(parameterName, value);
            return this;
        }

        public Parameters build() {
            return new Parameters(sender, aggregateId, offset, limit, filter, sorting, userId, skipPagination, params);
        }

    }

}
