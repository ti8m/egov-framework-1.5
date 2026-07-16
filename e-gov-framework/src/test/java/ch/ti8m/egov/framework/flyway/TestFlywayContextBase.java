package ch.ti8m.egov.framework.flyway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        TestFlywayContextBase.TestFlywayApplication.class
})
public class TestFlywayContextBase {
    @EntityScan(basePackages = {
            "ch.ti8m.egov.framework",
    })

    @SpringBootApplication()
    @ComponentScan(
            basePackages = "ch.ti8m.egov.framework",
            excludeFilters = {
                    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "ch\\.ti8m\\.egov\\.framework\\.testbase\\..*")
            }
    )
    protected static class TestFlywayApplication {
        public static void main(final String... args) {
            SpringApplication.run(TestFlywayApplication.class, args);
        }
    }

}
