package ch.ti8m.egov.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = {
        "ch.ti8m.egov.demo",
        "ch.ti8m.egov.framework",
        "ch.ti8m.egov.mdm"
})
@SpringBootApplication(scanBasePackages = {
        "ch.ti8m.egov.demo",
        "ch.ti8m.egov.framework",
        "ch.ti8m.egov.airulegeneration",
        "ch.ti8m.egov.mdm"
})
public class Main {

    public static void main(final String... args) {
        // nothing to do here
        System.out.println("Start eGovFramework as application for testing only ..");
        SpringApplication.run(Main.class, args);
    }

}
