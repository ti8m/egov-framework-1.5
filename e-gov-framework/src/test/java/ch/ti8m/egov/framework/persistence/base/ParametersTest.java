package ch.ti8m.egov.framework.persistence.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParametersTest {

    @Test
    void add() {
        final Parameters parameters = new Parameters();
        parameters.add("foo", "bar");

        assertThat(parameters.getParameter("foo")).isEqualTo("bar");
    }

    @Test
    void getParameter_noParams_returnNull() {
        final Parameters parameters = new Parameters();

        assertThat(parameters.getParameter("foo")).isNull();
    }

    @Test
    void getParameterAs_noParams_returnNull() {
        final Parameters parameters = new Parameters();

        final Long aLong = parameters.getParameterAs("foo");

        assertThat(aLong).isNull();
    }

    @Test
    void jsonRoundTrip_preservesParams() throws Exception {
        // ObjectMapper config matches the one used by ParametersConverter and
        // by inbound Spring MVC deserialisation defaults: no extra modules
        // are needed for the params map itself because callers stringify
        // non-primitive values before adding them (see SetMIMDatumComponent
        // in curiaplus-backend for the LocalDateTime case).
        final ObjectMapper objectMapper = JsonMapper.builder().build();

        final Parameters parameters = Parameters.builder()
            .add("personId", 42)
            .add("traceIdMIMSync", "abc-123")
            .build();

        final String json = objectMapper.writeValueAsString(parameters);

        assertThat(json)
            .as("params map must be present in JSON so the remote command "
                + "receiver and the ParametersConverter can read it")
            .contains("\"params\"")
            .contains("\"personId\"");

        final Parameters roundTripped =
            objectMapper.readValue(json, Parameters.class);
        assertThat((Integer) roundTripped.getParameterAs("personId"))
            .isEqualTo(42);
        assertThat((String) roundTripped.getParameterAs("traceIdMIMSync"))
            .isEqualTo("abc-123");
    }
}
