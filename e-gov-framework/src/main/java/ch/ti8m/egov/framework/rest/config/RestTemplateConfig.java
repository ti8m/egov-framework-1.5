package ch.ti8m.egov.framework.rest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    public static final String RCI_REST_TEMPLATE_QUALIFIER = "rciRestTemplate";
    private final ObjectMapper objectMapper;

    public RestTemplateConfig(@Qualifier(ObjectMapperConfig.REST_TEMPLATE_OBJECT_MAPPER) ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean(RCI_REST_TEMPLATE_QUALIFIER)
    public RestTemplate defaultRestTemplate() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        final RestTemplate restTemplate = new RestTemplate();
        final List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(converter);
        messageConverters.addAll(restTemplate.getMessageConverters());
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

    @Bean
    public RCIHttpHeaderProvider defaultRCIHttpHeaderProvider() {
        return () -> {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return headers;
        };
    }

}
