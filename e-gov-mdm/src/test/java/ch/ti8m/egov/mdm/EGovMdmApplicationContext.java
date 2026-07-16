package ch.ti8m.egov.mdm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        EGovMdmApplicationContext.EGovMdmApplication.class
})
@DirtiesContext
public class EGovMdmApplicationContext {

    @EntityScan(basePackages = {
            "ch.ti8m.egov.framework",
            "ch.ti8m.egov.mdm"
    })
    @SpringBootApplication(scanBasePackages = {
            "ch.ti8m.egov.framework",
            "ch.ti8m.egov.mdm"
    })
    protected static class EGovMdmApplication {
        public static void main(final String... args) {
            SpringApplication.run(EGovMdmApplicationContext.class, args);
        }
    }
}
