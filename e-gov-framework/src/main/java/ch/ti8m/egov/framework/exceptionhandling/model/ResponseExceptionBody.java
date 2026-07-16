package ch.ti8m.egov.framework.exceptionhandling.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ResponseExceptionBody {

    private final String message;
    private final UUID exceptionId;
    private final String errorCode;
    private final Map<String, Object> additionalInfo;
    private final String stackTrace;
    private final String link;

}
