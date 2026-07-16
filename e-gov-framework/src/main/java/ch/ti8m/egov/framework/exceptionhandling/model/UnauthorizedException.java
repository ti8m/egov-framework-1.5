package ch.ti8m.egov.framework.exceptionhandling.model;

import lombok.Getter;

import java.util.Map;

@Getter
public class UnauthorizedException extends EGovException {

    private final Map<String, Object> additionalInfo;

    public UnauthorizedException(final String message, final Map<String, Object> additionalInfo) {
        super(message);
        this.additionalInfo = additionalInfo;
    }

}
