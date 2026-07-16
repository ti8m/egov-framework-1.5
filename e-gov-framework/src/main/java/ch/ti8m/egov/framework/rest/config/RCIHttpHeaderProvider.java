package ch.ti8m.egov.framework.rest.config;

import org.springframework.http.HttpHeaders;

@FunctionalInterface
public interface RCIHttpHeaderProvider {
    HttpHeaders provideHttpHeaders();
}
