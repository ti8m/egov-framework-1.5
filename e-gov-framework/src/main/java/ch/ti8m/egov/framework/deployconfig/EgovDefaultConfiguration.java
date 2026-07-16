package ch.ti8m.egov.framework.deployconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:egov-default.yml")
public class EgovDefaultConfiguration {

}
