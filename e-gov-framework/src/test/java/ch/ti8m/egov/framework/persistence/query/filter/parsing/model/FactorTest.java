package ch.ti8m.egov.framework.persistence.query.filter.parsing.model;

import ch.ti8m.egov.framework.deployconfig.DatabaseConfigurationService;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.persistence.query.JoinConfiguration;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.join.TableJoins;
import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import ch.ti8m.egov.testbase.TestApplicationContext;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class FactorTest extends TestApplicationContext {

    @Autowired
    private NameTranslationComponent nameTranslationComponent;

    @Autowired
    private DatabaseConfigurationService databaseConfigurationService;

    private JoinConfiguration defaultJoinConfiguration;

    @BeforeEach
    void setUp() {
        defaultJoinConfiguration = new JoinConfiguration(
                false,
                false,
                true,
                databaseConfigurationService.getFalseStatement());
    }

    @Test
    void correctParameter_number() {
        final Factor factor = Factor.builder()
                .expression(Optional.empty())
                .filter(Optional.of("a == 12"))
                .build();

        final Triple<String, List<Object>, TableJoins> result = factor.toSqlString(
                TestEntity.class,
                new ArrayList<>(),
                new TableJoins(),
                nameTranslationComponent,
                defaultJoinConfiguration);

        assertThat(result.getMiddle()).containsExactly(
                12L
        );
    }

    @Test
    void correctParameter_boolean() {
        final Factor factor = Factor.builder()
                .expression(Optional.empty())
                .filter(Optional.of("a == true"))
                .build();

        final Triple<String, List<Object>, TableJoins> result = factor.toSqlString(
                TestEntity.class,
                new ArrayList<>(),
                new TableJoins(),
                nameTranslationComponent,
                defaultJoinConfiguration);

        assertThat(result.getMiddle()).containsExactly(
                true
        );
    }

    @Test
    void correctParameter_null() {
        final Factor factor = Factor.builder()
                .expression(Optional.empty())
                .filter(Optional.of("a IS NULL"))
                .build();

        final Triple<String, List<Object>, TableJoins> result = factor.toSqlString(
                TestEntity.class,
                new ArrayList<>(),
                new TableJoins(),
                nameTranslationComponent,
                defaultJoinConfiguration);

        assertThat(result.getLeft()).isEqualTo("(rootTable.a IS NULL)");
    }

    @Test
    void correctParameter_not_null() {
        final Factor factor = Factor.builder()
                .expression(Optional.empty())
                .filter(Optional.of("a IS NOT NULL"))
                .build();

        final Triple<String, List<Object>, TableJoins> result = factor.toSqlString(
                TestEntity.class,
                new ArrayList<>(),
                new TableJoins(),
                nameTranslationComponent,
                defaultJoinConfiguration);

        assertThat(result.getLeft()).isEqualTo("(rootTable.a IS NOT NULL)");
    }

    @Test
    void correctParameter_in_string() {
        final Factor factor = Factor.builder()
                .expression(Optional.empty())
                .filter(Optional.of("a IN ('a', 'b', 'c')"))
                .build();

        final Triple<String, List<Object>, TableJoins> result = factor.toSqlString(
                TestEntity.class,
                new ArrayList<>(),
                new TableJoins(),
                nameTranslationComponent,
                defaultJoinConfiguration);

        assertThat(result.getLeft()).isEqualTo("(rootTable.a IN (?,?,?))");
        assertThat(result.getMiddle()).containsExactly(
                "a",
                "b",
                "c"
        );
    }

    @Test
    void correctParameter_in_number() {
        final Factor factor = Factor.builder()
                .expression(Optional.empty())
                .filter(Optional.of("a IN (1, 2, 3)"))
                .build();

        final Triple<String, List<Object>, TableJoins> result = factor.toSqlString(
                TestEntity.class,
                new ArrayList<>(),
                new TableJoins(),
                nameTranslationComponent,
                defaultJoinConfiguration);

        assertThat(result.getLeft()).isEqualTo("(rootTable.a IN (1, 2, 3))");
        assertThat(result.getMiddle()).isEmpty();
    }

    private static class TestEntity extends ModifiableEntity {
        private String a;

    }

}
