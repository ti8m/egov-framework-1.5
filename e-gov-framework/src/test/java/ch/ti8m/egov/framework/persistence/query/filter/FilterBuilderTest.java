package ch.ti8m.egov.framework.persistence.query.filter;

import ch.ti8m.egov.framework.deployconfig.DatabaseConfigurationService;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.persistence.query.JoinConfiguration;
import ch.ti8m.egov.framework.persistence.query.ParametrizedQuery;
import ch.ti8m.egov.testbase.TestApplicationContext;
import jakarta.persistence.Column;
import lombok.Data;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FilterBuilderTest extends TestApplicationContext {

    @Autowired
    private FilterBuilder filterBuilder;

    @Autowired
    private DatabaseConfigurationService databaseConfigurationService;

    @Test
    void parseClassWithColumnAnnotationWithoutName() {
        JoinConfiguration joinConfiguration = new JoinConfiguration(
                true,
                true,
                true,
                databaseConfigurationService.getFalseStatement());

        final ParametrizedQuery result = filterBuilder.getFilterQuery(
                "",
                "(standard == true)",
                TestEntity.class,
                joinConfiguration);

        Assertions.assertThat(result.getQuery()).isEqualTo(" WHERE ((((((rootTable.standard = ?))))))");
        Assertions.assertThat(result.getParameters()).containsExactly(true);
    }

    @Data
    private class TestEntity extends ModifiableEntity {

        @Column(nullable = false)
        private boolean standard;

    }

}
