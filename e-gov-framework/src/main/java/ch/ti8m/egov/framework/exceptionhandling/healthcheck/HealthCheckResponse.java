package ch.ti8m.egov.framework.exceptionhandling.healthcheck;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthCheckResponse {

    private String version;
    private Status status;
    private String description;
    private String output;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime time;
    private String serviceId;
    private String buildTime;
    private Map<String, Component> checks;
    public enum Status {
        @JsonProperty("pass")
        PASS,
        @JsonProperty("warn")
        WARN,
        @JsonProperty("fail")
        FAIL;

        public static Status of(final System.Logger.Level level) {
            switch (level) {
                case ERROR:
                    return FAIL;
                case ALL:
                    return PASS;
                case WARNING:
                default:
                    return WARN;
            }
        }
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Component {
        private String componentType;
        private String componentId;
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private LocalDateTime time;
        private Status status;
        private String observedUnit;
        private Number observedValue;
        private String output;
    }
}
