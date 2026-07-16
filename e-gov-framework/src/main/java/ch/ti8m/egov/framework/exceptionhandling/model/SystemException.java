package ch.ti8m.egov.framework.exceptionhandling.model;

import java.util.Map;

public class SystemException extends EGovException {

    public SystemException(final String code, final String message, final Map<String, Object> additionalInfo) {
        super(code, message, additionalInfo);
    }

    public SystemException(final String code, final String message, final Throwable cause) {
        super(code, message, cause);
    }

    public SystemException(final String code, final String message) {
        super(code, message);
    }

}
