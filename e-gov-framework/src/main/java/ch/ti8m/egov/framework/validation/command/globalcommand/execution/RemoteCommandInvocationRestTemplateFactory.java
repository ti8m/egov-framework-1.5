package ch.ti8m.egov.framework.validation.command.globalcommand.execution;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;

@Configuration
public class RemoteCommandInvocationRestTemplateFactory {

    @Bean
    public HttpHeaders getRestTemplate() {
        return new HttpHeaders() {{
            final String auth = "testcp.zs-intma@ti8m.ch:ti8m_2020!"; //todo: replace with service user
            final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII), false);
            final String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }

}
