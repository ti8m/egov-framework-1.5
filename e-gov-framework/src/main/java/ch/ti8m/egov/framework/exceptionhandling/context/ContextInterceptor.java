package ch.ti8m.egov.framework.exceptionhandling.context;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class ContextInterceptor implements HandlerInterceptor {

    public static final String FILTER = "filter";
    public static final String SORT = "sort";
    public static final String OFFSET = "offset";
    public static final String LIMIT = "limit";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String SEARCH = "search";
    private static final String X_FORWARDED_FOR = "x-forwarded-for";

    private final UserIdProvider userIdProvider;

    @Autowired
    public ContextInterceptor(final UserIdProvider userIdProvider) {
        this.userIdProvider = userIdProvider;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        try {
            final Map<String, String[]> queryParameters = request.getParameterMap();
            DataHolder.initialize(
                    userIdProvider.getUserId(request),
                    queryParameters.containsKey(FILTER) ? queryParameters.get(FILTER)[0] : null,
                    queryParameters.containsKey(SORT) ? getSortString(queryParameters.get(SORT)) : null,
                    queryParameters.containsKey(OFFSET) ? Integer.parseInt(queryParameters.get(OFFSET)[0]) : null,
                    queryParameters.containsKey(LIMIT) ? Integer.parseInt(queryParameters.get(LIMIT)[0]) : null,
                    queryParameters.containsKey(PAGE) ? Integer.parseInt(queryParameters.get(PAGE)[0]) : null,
                    queryParameters.containsKey(SIZE) ? Integer.parseInt(queryParameters.get(SIZE)[0]) : null,
                    queryParameters.containsKey(SEARCH) ? queryParameters.get(SEARCH)[0] : null,
                    request.getHeader(X_FORWARDED_FOR) != null ? request.getHeader(X_FORWARDED_FOR) : null,
                    request.getRequestURL() == null ? "" : request.getRequestURL().toString()
            );
            DataHolder.setContentLanguage(request.getHeader(HttpHeaders.CONTENT_LANGUAGE) == null
                    ? Locale.ENGLISH.getLanguage()
                    : request.getHeader(HttpHeaders.CONTENT_LANGUAGE));
            return HandlerInterceptor.super.preHandle(request, response, handler);
        } catch (final Exception e) {
            throw new EGovException(e);
        }
    }

    private String getSortString(final String[] sortArray) {
        return String.join(",", sortArray);
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) {
        try {
            HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
            if (log.isDebugEnabled()) {
                log.debug("Removing DataHolder with UserId: " + DataHolder.getUserId()
                        + "  from Thread: " + Thread.currentThread().getId());
            }
            DataHolder.cleanUp();
        } catch (final Exception e) {
            throw new EGovException(e);
        }
    }

}
