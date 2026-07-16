package ch.ti8m.egov.framework.validation.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SpringContext {
    private static Environment environment;

    @Autowired
    public SpringContext(final Environment environment) {
        SpringContext.environment = environment;
    }

    public static String getProperty(final String key, final String defaultValue) {
        return SpringContext.environment.getProperty(key, defaultValue);
    }

}
