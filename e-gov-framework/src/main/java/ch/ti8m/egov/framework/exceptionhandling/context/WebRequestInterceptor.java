package ch.ti8m.egov.framework.exceptionhandling.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebRequestInterceptor implements WebMvcConfigurer {

    private final ContextInterceptor contextInterceptor;

    @Autowired
    public WebRequestInterceptor(ContextInterceptor contextInterceptor) {
        this.contextInterceptor = contextInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(contextInterceptor);
    }
}
