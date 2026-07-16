package ch.ti8m.egov.framework.rest.config;

import ch.ti8m.egov.testbase.TestApplicationContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest
@ActiveProfiles("test")
class RestTemplateConfigTest extends TestApplicationContext {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper defaultObjectMapper;

    private static final String JSON = "{\"message\":\"hola\", \"intNumber\":47}";

    @Test
    void restTemplateShouldUseCustomObjectMapperForLocalDateTime() throws Exception {
        final TestDto dto = new TestDto();
        dto.setMessage("Hello");
        dto.setTimestamp(LocalDateTime.of(2025, 4, 24, 13, 45));

        final MappingJackson2HttpMessageConverter jacksonConverter = restTemplate.getMessageConverters().stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst()
                .orElseThrow();

        final String json = jacksonConverter.getObjectMapper().writeValueAsString(dto);
        assertThat(json).contains("\"timestamp\":\"2025-04-24T13:45:00\"");
        assertThat(json).doesNotContain("[2025,4,24"); // Ensure array-style is not used
    }

    @Test
    void restTemplateShouldUseCustomObjectMapperWithLongForInts(){

        final MappingJackson2HttpMessageConverter jacksonConverter = restTemplate.getMessageConverters().stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst()
                .orElseThrow();

        final ObjectMapper objectMapper = jacksonConverter.getObjectMapper();

        try{
            Map<String, Object> map = objectMapper.readValue(JSON, new TypeReference<>() {});
            Object number = map.get("intNumber");

            assertThat(number).isInstanceOf(Long.class);
            assertThat(number).isEqualTo(47L);
        } catch (JsonProcessingException e){
            fail("Deserialization failed: " + e.getMessage());
        }

    }

    @Test
    void normalObjectMapperShouldUseIntegerForInts(){
        try{
            Map<String, Object> map = defaultObjectMapper.readValue(JSON, new TypeReference<>() {});
            Object number = map.get("intNumber");

            assertThat(number).isInstanceOf(Integer.class);
            assertThat(number).isEqualTo(47);
        } catch (JsonProcessingException e){
            fail("Deserialization failed: " + e.getMessage());
        }
    }

    @Setter
    @Getter
    static class TestDto {
        private String message;
        private LocalDateTime timestamp;
    }
}
