package ch.ti8m.egov.framework.rest.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@RequiredArgsConstructor
public class ObjectMapperConfig {

    public final static String REST_TEMPLATE_OBJECT_MAPPER = "restTemplateObjectMapper";

    @Bean
    @Primary
    public ObjectMapper defaultObjectMapper(final Jackson2ObjectMapperBuilder builder) {
        return builder.build();
    }

    @Bean(REST_TEMPLATE_OBJECT_MAPPER)
    public ObjectMapper restTemplateObjectMapper(final Jackson2ObjectMapperBuilder builder) {
        // use builder to provide default settings by spring boot
        final ObjectMapper objectMapper = builder.build();
        objectMapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        return objectMapper;
    }

}