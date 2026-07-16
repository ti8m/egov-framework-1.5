package ch.ti8m.egov.framework.exceptionhandling.context;

import jakarta.servlet.http.HttpServletRequest;

public interface UserIdProvider {

    String getUserId(final HttpServletRequest request);

}
