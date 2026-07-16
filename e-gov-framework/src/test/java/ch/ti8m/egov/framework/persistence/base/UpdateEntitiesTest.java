package ch.ti8m.egov.framework.persistence.base;

import ch.ti8m.egov.testbase.entities.relationship.TestEntity;
import ch.ti8m.egov.testbase.entities.relationship.TestSubEntityOneToMany;
import ch.ti8m.egov.testbase.repositories.TestEntityRepository;
import ch.ti8m.egov.testbase.repositories.TestSubEntityOneToManyRepository;
import ch.ti8m.egov.testbase.tools.RepositoryTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test-sqlserver")
class SQLServerUpdateEntitiesTest extends UpdateEntitiesTest {
}

@ActiveProfiles("test-postgres")
class PostgresUpdateEntitiesTest extends UpdateEntitiesTest {
}

@DirtiesContext
abstract class UpdateEntitiesTest extends RepositoryTestBase {

    @Autowired
    protected TestEntityRepository testEntityRepository;

    @Autowired
    protected TestSubEntityOneToManyRepository subEntityRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void testUpdateMultipleEntities() {
        testEntityRepository.deactivatePermissions();
        subEntityRepository.deactivatePermissions();
        final TestSubEntityOneToMany subEntity = TestSubEntityOneToMany.builder().content("SubEntity1").build();
        final TestEntity testEntity = TestEntity.builder().firstname("Name1").build();
        testEntity.addTestSubEntityOneToMany(subEntity);
        final Long testEntityId = testEntityRepository.saveWithTx(testEntity);

        assertThat(testEntity.getId()).isNotNull();
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            final TestSubEntityOneToMany retrievedSubEntity = subEntityRepository.findById(subEntity.getId()).orElseThrow();
            final TestEntity retrievedEntity = testEntityRepository.findById(testEntityId).orElseThrow();

            assertThat(retrievedEntity.getClass().getName()).contains("HibernateProxy");

            retrievedEntity.setFirstname("Name1Updated");

            final TestEntity updatedEntity = testEntityRepository.update(retrievedEntity);
            assertThat(updatedEntity.getFirstname()).isEqualTo("Name1Updated");
        });
    }
}
