package ch.ti8m.egov.framework.persistence.base;

import ch.ti8m.egov.testbase.entities.inheritance.mapped_superclass.TestMappedImplementingEntity;
import ch.ti8m.egov.testbase.entities.inheritance.table_per_class.TestInheritanceTablePerClassImplementingEntity;
import ch.ti8m.egov.testbase.entities.inheritance.table_per_class.TestSuperSubEntity;
import ch.ti8m.egov.testbase.tools.RepositoryTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test-sqlserver")
class SQLServerEntityInheritanceTest extends EntityInheritanceTest {
}

@ActiveProfiles("test-postgres")
class PostgresEntityInheritanceTest extends EntityInheritanceTest {
}

@Transactional
abstract class EntityInheritanceTest extends RepositoryTestBase {

    @Test
    void inheritedEntity_crud_parentFieldsfound() {
        testImplementingEntityRepository.deactivatePermissionsGlobally();

        final var testEntity = new TestMappedImplementingEntity("implFieldContent", "superFieldContent");
        final Long id = testImplementingEntityRepository.saveWithTx(testEntity);
        final var retrievedEntity = testImplementingEntityRepository.findById(id);
        assertThat(retrievedEntity).isPresent();
        assertThat(retrievedEntity.get().getSuperField()).isEqualTo(testEntity.getSuperField());
        assertThat(retrievedEntity.get().getImplementingField()).isEqualTo(testEntity.getImplementingField());

    }

    @Test
    void inheritedTablePerClassEntity_crud_parentFieldsFound() {
        testTablePerClassImplementingEntityRepository.deactivatePermissionsGlobally();

        final var testEntity = new TestInheritanceTablePerClassImplementingEntity("implFieldContent", "superFieldContent");
        final var mappedEntity = TestSuperSubEntity.builder()
                .mappedEntityContentField("mappedEntityContent")
                .testTablePerClassSuperEntity(testEntity)
                .build();
        testEntity.setTestMappedToSuperClassEntities(List.of(mappedEntity));

        final Long id = testTablePerClassImplementingEntityRepository.saveWithTx(testEntity);
        final var retrievedEntity = testTablePerClassImplementingEntityRepository.findById(id);
        assertThat(retrievedEntity).isPresent();
        assertThat(retrievedEntity.get().getSuperField()).isEqualTo(testEntity.getSuperField());
        assertThat(retrievedEntity.get().getImplementingField()).isEqualTo(testEntity.getImplementingField());
        assertThat(retrievedEntity.get().getTestMappedToSuperClassEntities()).hasSize(1);
        assertThat(retrievedEntity.get().getTestMappedToSuperClassEntities().get(0).getMappedEntityContentField()).isEqualTo(mappedEntity.getMappedEntityContentField());

    }
}
