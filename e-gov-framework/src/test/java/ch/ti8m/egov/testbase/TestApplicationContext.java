package ch.ti8m.egov.testbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(classes = {
        TestApplicationContext.TestApplication.class
})
public class TestApplicationContext {


    @EntityScan(basePackages = {
            "ch.ti8m.egov.framework",
            "ch.ti8m.egov.testbase"
    })
    @SpringBootApplication(scanBasePackages = {
            "ch.ti8m.egov.framework",
            "ch.ti8m.egov.testbase"

    })
    protected static class TestApplication {
        public static void main(String... args) {
            SpringApplication.run(TestApplicationContext.TestApplication.class, args);
        }
    }
}
