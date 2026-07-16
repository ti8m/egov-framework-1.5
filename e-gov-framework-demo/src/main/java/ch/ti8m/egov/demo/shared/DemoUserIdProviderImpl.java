package ch.ti8m.egov.demo.shared;

import ch.ti8m.egov.framework.exceptionhandling.context.UserIdProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DemoUserIdProviderImpl implements UserIdProvider {

    private static final String USER_ID_PARAMETER = "userId";

    @Override
    public String getUserId(HttpServletRequest request) {
        return request.getParameterMap().containsKey(USER_ID_PARAMETER)
                ? request.getParameter(USER_ID_PARAMETER)
                : "N/A";
    }
}
