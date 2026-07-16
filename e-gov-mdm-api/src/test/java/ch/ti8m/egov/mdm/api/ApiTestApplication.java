package ch.ti8m.egov.mdm.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;

@SpringBootApplication(
scanBasePackages = {
        "ch.ti8m.egov.framework",
        "ch.ti8m.egov.mdm"
}
)
@EntityScan(basePackages = {
        "ch.ti8m.egov.framework",
        "ch.ti8m.egov.mdm"
})
@Slf4j
public class ApiTestApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext applicationContext = SpringApplication.run(ApiTestApplication.class, args);
        final ConfigurableEnvironment appEnvironment = applicationContext.getEnvironment();

        log.info("Application started with profile(s): {}", Arrays.toString(appEnvironment.getActiveProfiles()));
        log.info("The swagger-ui can be found here: http://localhost:{}/swagger-ui.html", appEnvironment.getProperty("server.port"));

    }

}
