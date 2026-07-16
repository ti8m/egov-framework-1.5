package ch.ti8m.egov.framework.exceptionhandling.context.defaults;

import ch.ti8m.egov.framework.exceptionhandling.context.UserIdProvider;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

@Component
public class DefaultUserIdProviderImpl implements UserIdProvider {
    private static final String COOKIE_NAME = "EGOV_TOKEN";

    @Override
    public String getUserId(HttpServletRequest request) {
        try {
            if (request.getCookies() != null) {
                for (final Cookie cookie : request.getCookies()) {
                    if (COOKIE_NAME.equals(cookie.getName())) {
                        final String payload = cookie.getValue().split("\\.")[1];
                        final Base64.Decoder decoder = Base64.getUrlDecoder();
                        final ObjectMapper objectMapper = new ObjectMapper();
                        final Token token = objectMapper.readValue(decoder.decode(payload), Token.class);
                        return token.getUserId();
                    }
                }
            }
            return "N/A";
        } catch (final IOException ioException) {
            throw new EGovException(ioException);
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static final class Token {

        private String userId;

    }
}
