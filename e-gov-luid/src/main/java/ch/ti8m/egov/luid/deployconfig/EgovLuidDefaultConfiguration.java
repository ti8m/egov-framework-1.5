package ch.ti8m.egov.luid.deployconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:egov-luid-default.yml")
public class EgovLuidDefaultConfiguration {

}
