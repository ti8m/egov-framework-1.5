package ch.ti8m.egov.framework.exceptionhandling.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Slf4j
public class EGovException extends RuntimeException {

    private String code;
    private Map<String, Object> additionalInfo = new HashMap<>();

    public EGovException(final String code, final String message, final Map<String, Object> additionalInfo) {
        super(message);
        setCode(code);
        this.additionalInfo.putAll(additionalInfo);
    }

    public EGovException(final String code, final String message, final Throwable cause) {
        super(message, cause);
        setCode(code);
    }

    public EGovException(final String code, final String message, final Object... additionalInfo) {
        this(code, message, convertToMap(additionalInfo));
    }


    public EGovException(final String code, final String message) {
        this(code, message, new HashMap<>());
    }


    public EGovException(final Throwable e) {
        super(e);
        setCode(ExceptionCode.DEFAULT);
    }

    public EGovException(final String message) {
        this(ExceptionCode.DEFAULT, message, new HashMap<>());
    }

    private static Map<String, Object> convertToMap(final Object... values) {
        if (values == null || values.length % 2 != 0) {
            return new HashMap<>();
        }
        final Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            if (values[i] instanceof final String key) {
                final Object value = values[i + 1];
                map.put(key, value);
            }
        }
        return map;
    }

    private void setCode(final String code) {
        if (code == null) {
            throw new IllegalArgumentException("No exception code provided");
        }
        this.code = code;
    }

    @Override
    public String getMessage() {
        String exceptionMessage = super.getMessage();
        if (Objects.nonNull(additionalInfo) && !additionalInfo.isEmpty()) {
            final ObjectMapper mapper = new ObjectMapper();
            try {
                exceptionMessage += " " + mapper.writeValueAsString(additionalInfo);
            } catch (final JsonProcessingException e) {
                log.error("failed to convert additional exception message of exception: {}", exceptionMessage, e);
            }
        }
        return exceptionMessage;
    }

    public void addAdditionalInfo(final String key, final String value) {
        if (key == null) {
            return;
        }
        if (this.additionalInfo == null) {
            this.additionalInfo = new HashMap<>();
        }
        this.additionalInfo.put(key, value);
    }
}