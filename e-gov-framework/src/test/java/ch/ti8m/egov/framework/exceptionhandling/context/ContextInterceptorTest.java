package ch.ti8m.egov.framework.exceptionhandling.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class ContextInterceptorTest {

    @Mock
    private UserIdProvider userIdProvider;

    @InjectMocks
    private ContextInterceptor contextInterceptor;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Test
    public void correctSort_commaSeparatedInput() {
        final Map<String, String[]> parameters = new HashMap<>();
        parameters.put(ContextInterceptor.SORT, new String[]{"test1:asc,test2:desc"});
        Mockito.when(request.getParameterMap()).thenReturn(parameters);

        contextInterceptor.preHandle(request, response, null);

        Assertions.assertThat(DataHolder.getSorting()).isEqualTo("test1:asc,test2:desc");
    }

    @Test
    void correctSort_arraySeparatedInput() {
        final Map<String, String[]> parameters = new HashMap<>();
        parameters.put(ContextInterceptor.SORT, new String[]{"test1:asc", "test2:desc"});
        Mockito.when(request.getParameterMap()).thenReturn(parameters);

        contextInterceptor.preHandle(request, response, null);

        Assertions.assertThat(DataHolder.getSorting()).isEqualTo("test1:asc,test2:desc");
    }

}