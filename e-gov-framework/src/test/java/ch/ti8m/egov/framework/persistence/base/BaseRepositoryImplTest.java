package ch.ti8m.egov.framework.persistence.base;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.testbase.entities.relationship.TestEntity;
import ch.ti8m.egov.testbase.tools.RepositoryTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ch.ti8m.egov.testbase.tools.EntityFactory.newMoritzEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;


@ActiveProfiles("test-sqlserver")
class SQLServerBaseRepositoryImplTest extends BaseRepositoryImplTest {
}

@ActiveProfiles("test-postgres")
class PostgresBaseRepositoryImplTest extends BaseRepositoryImplTest {
}


abstract class BaseRepositoryImplTest extends RepositoryTestBase {

    @Test
    void testGlobalRepositoryDeactivation() {
        repository.activatePermissions();
        repository.deactivatePermissionsGlobally();

        new Thread(() -> {
            assertThat(DataHolder.getRepositoryInstanceContext(repository.toString()).isSkipPermissions()).isFalse();
            assertThat(repository.isPermissionsDeactivated()).isTrue();
        });
    }

    @Test
    void findBy_fieldIsBoolean() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);
        final List<TestEntity> retrievedEntities = repository.findByFields(
                TestEntity.Fields.active,
                insertEntity.isActive()
        );

        assertThat(retrievedEntities).hasSize(1);
        final TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
    }

    @Test
    void whenNoIdsGiven_thenFindByIdReturnsEmptyList() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);

        assertWith(repository.findAllById(Collections.emptyList()), result -> assertThat(result).isEmpty());
    }

    @Test
    void whenNullIdsGiven_thenFindByIdReturnsEmptyList() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);

        assertWith(repository.findAllById(null), result -> assertThat(result).isEmpty());
    }

    @Test
    void whenIdGiven_thenFindByIdReturnsElement() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);

        assertWith(repository.findAllById(List.of(insertEntity.getId())), result -> {
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(insertEntity.getId());
        });
    }

    @Test
    void findBy_fieldIsLocalDateTime() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);
        final List<TestEntity> retrievedEntities = repository.findByFields(
                TestEntity.Fields.localDateTime,
                insertEntity.getLocalDateTime()
        );

        assertThat(retrievedEntities).hasSize(1);
        final TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
    }

    @Test
    void findBy_fieldIsZonedDateTime() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);
        final List<TestEntity> retrievedEntities = repository.findByFields(
                TestEntity.Fields.zonedDateTime,
                insertEntity.getZonedDateTime()
        );

        assertThat(retrievedEntities).hasSize(1);
        final TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
    }

    @Test
    void findWith_oneFilter() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);
        final List<TestEntity> retrievedEntities = repository.findWithFilter(
                TestEntity.Fields.firstname + " == '" + insertEntity.getFirstname() + "'"
        );

        assertThat(retrievedEntities).hasSize(1);
        final TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
    }

    @Test
    void findWith_twoFilters() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);
        final List<TestEntity> retrievedEntities = repository.findWithFilter(
                TestEntity.Fields.firstname + " == '" + insertEntity.getFirstname() + "'",
                TestEntity.Fields.lastname + " == '" + insertEntity.getLastname() + "'"
        );

        assertThat(retrievedEntities).hasSize(1);
        final TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
    }

    @Test
    void findWith_oneFilter_dateLessThan() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);
        final List<TestEntity> retrievedEntities = repository.findWithFilter(
                TestEntity.Fields.localDateTime + " < " + insertEntity.getLocalDateTime().plusDays(1)
        );

        assertThat(retrievedEntities).hasSize(1);
        final TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
    }

    @Test
    void findWith_oneFilter_dateGreaterThan_negative() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);
        final List<TestEntity> retrievedEntities = repository.findWithFilter(
                TestEntity.Fields.localDateTime + " > " + insertEntity.getLocalDateTime().plusDays(1)
        );

        assertThat(retrievedEntities).isEmpty();
    }

    @Test
    void findWith_nullFilterIgnored() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);
        final List<TestEntity> retrievedEntities = repository.findWithFilter(
                TestEntity.Fields.firstname + " == '" + insertEntity.getFirstname() + "'",
                null
        );

        assertThat(retrievedEntities).hasSize(1);
        final TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
    }

    @Test
    void findWith_emptyFilterIgnored() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();

        repository.saveWithTx(insertEntity);
        final List<TestEntity> retrievedEntities = repository.findWithFilter(
                TestEntity.Fields.firstname + " == '" + insertEntity.getFirstname() + "'",
                ""
        );

        assertThat(retrievedEntities).hasSize(1);
        final TestEntity retrievedEntity = retrievedEntities.get(0);
        assertThat(insertEntity.getId()).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(insertEntity.getId());
    }

    @Test
    void find_archivedNotFound() {
        repository.deactivatePermissions();

        final TestEntity insertEntity = newMoritzEntity();
        insertEntity.setArchived(true);
        final Long id = repository.saveWithTx(insertEntity);
        final Optional<TestEntity> found = repository.findById(id);

        assertThat(found).isEmpty();

    }
}
