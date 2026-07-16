package ch.ti8m.egov.framework.persistence.base;

import ch.ti8m.egov.testbase.entities.relationship.TestEntity;
import ch.ti8m.egov.testbase.entities.relationship.TestSubEntityOneToMany;
import ch.ti8m.egov.testbase.tools.RepositoryTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.StringJoiner;

import static ch.ti8m.egov.testbase.tools.EntityFactory.newMoritzEntity;
import static ch.ti8m.egov.testbase.tools.EntityFactory.newNullFieldEntity;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test-sqlserver")
class SQLServerBaseRepositoryImplFindByTest extends BaseRepositoryImplFindByTest {
}

@ActiveProfiles("test-postgres")
class PostgresBaseRepositoryImplFindByTest extends BaseRepositoryImplFindByTest {
}

abstract class BaseRepositoryImplFindByTest extends RepositoryTestBase {

    @Test
    void findByFields_fieldOnAggregate_discreteValue() {
        repository.deactivatePermissions();
        // add other entities that should not be found
        repository.saveWithTx(newNullFieldEntity());
        // testentity
        TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);
        List<TestEntity> retrievedEntities = repository.findByFields(TestEntity.Fields.id, insertEntity.getId());

        assertThat(retrievedEntities).hasSize(1);
        TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
        assertThat(retrievedEntity.getFirstname()).isEqualTo(insertEntity.getFirstname());
        assertThat(retrievedEntity.getLastname()).isEqualTo(insertEntity.getLastname());
    }

    @Test
    void findByFields_fieldOnAggregate_nullValue() {
        repository.deactivatePermissions();
        // add other entities that should not be found
        repository.saveWithTx(newMoritzEntity());
        // testentity
        TestEntity insertEntity = newNullFieldEntity();

        repository.saveWithTx(insertEntity);
        List<TestEntity> retrievedEntities = repository.findByFields(TestEntity.Fields.firstname, insertEntity.getFirstname());

        assertThat(retrievedEntities).hasSize(1);
        TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
        assertThat(retrievedEntity.getFirstname()).isEqualTo(insertEntity.getFirstname());
        assertThat(retrievedEntity.getLastname()).isEqualTo(insertEntity.getLastname());
    }

    @Test
    void findByFields_fieldOnAggregate_combinedDiscreteValueAndNnullValue() {
        repository.deactivatePermissions();
        // add other entities that should not be found
        repository.saveWithTx(newMoritzEntity());
        repository.saveWithTx(newNullFieldEntity());
        // testentity
        TestEntity insertEntity = newMoritzEntity();
        insertEntity.setLastname(null);

        repository.saveWithTx(insertEntity);
        List<TestEntity> retrievedEntities = repository.findByFields(
                TestEntity.Fields.firstname, insertEntity.getFirstname(),
                TestEntity.Fields.lastname, insertEntity.getLastname()
        );

        assertThat(retrievedEntities).hasSize(1);
        TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
        assertThat(retrievedEntity.getFirstname()).isEqualTo(insertEntity.getFirstname());
        assertThat(retrievedEntity.getLastname()).isEqualTo(insertEntity.getLastname());
    }

    @Test
    void findByFields_fieldOnSubEntity_discreteValue() {
        repository.deactivatePermissions();
        // add other entities that should not be found
        repository.saveWithTx(newNullFieldEntity());
        // testentity
        TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);
        String fieldName = new StringJoiner(".")
                .add(TestEntity.Fields.testSubEntitiesOneToMany)
                .add(TestSubEntityOneToMany.Fields.content)
                .toString();
        List<TestEntity> retrievedEntities = repository.findByFields(
                fieldName,
                insertEntity.getTestSubEntitiesOneToMany().get(0).getContent()
        );

        assertThat(retrievedEntities).hasSize(1);
        TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
        assertThat(retrievedEntity.getFirstname()).isEqualTo(insertEntity.getFirstname());
        assertThat(retrievedEntity.getLastname()).isEqualTo(insertEntity.getLastname());
    }

    @Test
    void findByFields_fieldIsDbName() {
        repository.deactivatePermissions();
        // testentity
        TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);
        List<TestEntity> retrievedEntities = repository.findByFields(
                TestEntity.SOME_VALUE_COLUMN_NAME,
                insertEntity.getSomeValue()
        );

        assertThat(retrievedEntities).hasSize(1);
        TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
    }

    @Test
    void findByFields_fieldOnSubEntity_archivedNotFound() {
        repository.deactivatePermissions();

        TestEntity insertEntity = newMoritzEntity();
        TestSubEntityOneToMany archivedSubEntity = new TestSubEntityOneToMany();
        archivedSubEntity.setContent("Archived");
        archivedSubEntity.setArchived(true);

        repository.saveWithTx(insertEntity);
        String fieldName = new StringJoiner(".")
                .add(TestEntity.Fields.testSubEntitiesOneToMany)
                .add(TestSubEntityOneToMany.Fields.content)
                .toString();

        List<TestEntity> findByArchived = repository.findByFields(fieldName, "Archived");

        assertThat(findByArchived).isEmpty();
    }

}
