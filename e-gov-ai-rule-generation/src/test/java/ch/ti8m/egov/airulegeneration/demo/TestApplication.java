package ch.ti8m.egov.airulegeneration.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = {
        "ch.ti8m.egov.framework",
        "ch.ti8m.egov.airulegeneration"
})
@SpringBootApplication(scanBasePackages = {
        "ch.ti8m.egov.framework",
        "ch.ti8m.egov.airulegeneration"

})
public class TestApplication {
    public static void main(final String... args) {
        SpringApplication.run(TestApplication.class, args);
    }
}