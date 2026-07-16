package ch.ti8m.egov.framework.persistence.base;

import ch.ti8m.egov.testbase.entities.relationship.TestEntity;
import ch.ti8m.egov.testbase.repositories.ExecutingComponent;
import ch.ti8m.egov.testbase.tools.RepositoryTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test-sqlserver")
class SQLServerBaseRepositoryImplIT extends BaseRepositoryImplIT {
}

@ActiveProfiles("test-postgres")
class PostgresBaseRepositoryImplIT extends BaseRepositoryImplIT {
}

abstract class BaseRepositoryImplIT extends RepositoryTestBase {

    TestEntity testEntity;
    TestEntity testEntityWithId;

    @Autowired
    ExecutingComponent executingComponent;

    @BeforeEach
    void setUp() {
        repository.deactivatePermissionsGlobally();
        repository.deleteAllWithTx();
        testEntity = TestEntity.builder()
                .firstname("firstname1")
                .lastname("old-lastname1")
                .build();
        testEntityWithId = TestEntity.builder()
                .id(100L)
                .firstname("firstname2")
                .lastname("old-lastname2")
                .build();
    }

    @Test
    void contextLoads() {
        Assertions.assertThat(executingComponent).isNotNull();
    }

    // SAVE
    @Test
    void save_completeRollback() {
        try {
            executingComponent.save(testEntity, true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void save_successful() {
        try {
            executingComponent.save(testEntity, false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getId))
                    .containsExactlyInAnyOrder(testEntity.getId());
        }
    }

    @Test
    void saveWithTx_rollbackOnFailed() {
        try {
            executingComponent.saveWithTx(testEntity, true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getId))
                    .containsExactlyInAnyOrder(testEntity.getId());
        }
    }

    @Test
    void saveWithTx_successful() {
        try {
            executingComponent.saveWithTx(testEntity, false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getId))
                    .containsExactlyInAnyOrder(testEntity.getId());
        }
    }

    @Test
    void saveList_completeRollback() {
        try {
            executingComponent.save(List.of(testEntity), true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void saveList_successful() {
        try {
            executingComponent.save(List.of(testEntity), false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getId))
                    .containsExactlyInAnyOrder(testEntity.getId());
        }
    }

    @Test
    void saveWithTxList_rollbackOnFailed() {
        try {
            executingComponent.saveWithTx(List.of(testEntity), false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getId))
                    .containsExactlyInAnyOrder(testEntity.getId());
        }
    }

    @Test
    void saveWithTxList_successful() {
        try {
            executingComponent.saveWithTx(List.of(testEntity), false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getId))
                    .containsExactlyInAnyOrder(testEntity.getId());
        }
    }

    // UPDATE
    @Test
    void update_completeRollback() {
        repository.saveWithTx(testEntity);
        try {
            testEntity.setLastname("new-lastname");
            executingComponent.update(testEntity, true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getLastname))
                    .containsExactlyInAnyOrder("old-lastname1");
        }
    }

    @Test
    void update_successful() {
        repository.saveWithTx(testEntity);
        try {
            testEntity.setLastname("new-lastname");
            executingComponent.update(testEntity, false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getLastname))
                    .containsExactlyInAnyOrder("new-lastname");
        }
    }

    @Test
    void updateWithTx_rollbackOnFailed() {
        repository.saveWithTx(testEntity);
        try {
            testEntity.setLastname("new-lastname");
            executingComponent.updateWithTx(testEntity, true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getLastname))
                    .containsExactlyInAnyOrder("new-lastname");
        }
    }

    @Test
    void updateWithTx_successful() {
        repository.saveWithTx(testEntity);
        try {
            testEntity.setLastname("new-lastname");
            executingComponent.updateWithTx(testEntity, false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getLastname))
                    .containsExactlyInAnyOrder("new-lastname");
        }
    }

    @Test
    void updateList_completeRollback() {
        repository.saveWithTx(testEntity);
        try {
            testEntity.setLastname("new-lastname");
            executingComponent.update(List.of(testEntity), true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getLastname))
                    .containsExactlyInAnyOrder("old-lastname1");
        }
    }

    @Test
    void updateList_successful() {
        repository.saveWithTx(testEntity);
        try {
            testEntity.setLastname("new-lastname");
            executingComponent.update(List.of(testEntity), false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getLastname))
                    .containsExactlyInAnyOrder("new-lastname");
        }
    }

    @Test
    void updateWithTxList_rollbackOnFailed() {
        repository.saveWithTx(testEntity);
        try {
            testEntity.setLastname("new-lastname");
            executingComponent.updateWithTx(List.of(testEntity), true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getLastname))
                    .containsExactlyInAnyOrder("new-lastname");
        }
    }

    @Test
    void updateWithTxList_successful() {
        repository.saveWithTx(testEntity);
        try {
            testEntity.setLastname("new-lastname");
            executingComponent.updateWithTx(List.of(testEntity), false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getLastname))
                    .containsExactlyInAnyOrder("new-lastname");
        }
    }

    // DELETE
    @Test
    void deleteLongId_rollbackOnFailed() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.delete(testEntity.getId(), true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getId))
                    .containsExactlyInAnyOrder(testEntity.getId());
        }
    }

    @Test
    void deleteLongId_successful() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.delete(testEntity.getId(), false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteWithTxLongId_rollbackOnFailed() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.deleteWithTx(testEntity.getId(), true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteWithTxLongId_successful() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.deleteWithTx(testEntity.getId(), false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteIntId_rollbackOnFailed() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.delete(testEntity.getId().intValue(), true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getId))
                    .containsExactlyInAnyOrder(testEntity.getId());
        }
    }

    @Test
    void deleteIntId_successful() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.delete(testEntity.getId().intValue(), false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteWithTxIntId_rollbackOnFailed() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.deleteWithTx(testEntity.getId().intValue(), true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteWithTxIntId_successful() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.deleteWithTx(testEntity.getId().intValue(), false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteList_rollbackOnFailed() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.delete(List.of(testEntity), true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getId))
                    .containsExactlyInAnyOrder(testEntity.getId());
        }
    }

    @Test
    void deleteList_successful() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.delete(List.of(testEntity), false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteListWithTx_rollbackOnFailed() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.deleteWithTx(List.of(testEntity), true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteListWithTx_successful() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.deleteWithTx(List.of(testEntity), false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void delete_rollbackOnFailed() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.delete(testEntity, true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getId))
                    .containsExactlyInAnyOrder(testEntity.getId());
        }
    }

    @Test
    void delete_successful() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.delete(testEntity, false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteWithTx_rollbackOnFailed() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.deleteWithTx(testEntity, true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteWithTx_successful() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.deleteWithTx(testEntity, false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteAll_rollbackOnFailed() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.deleteAll(true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll().stream().map(TestEntity::getId))
                    .containsExactlyInAnyOrder(testEntity.getId());
        }
    }

    @Test
    void deleteAll_successful() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.deleteAll(false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteAllWithTx_rollbackOnFailed() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.deleteAllWithTx(true);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

    @Test
    void deleteAllWithTx_successful() {
        repository.saveWithTx(testEntity);
        try {
            executingComponent.deleteAllWithTx(false);
        } catch (final Exception ignore) {
        } finally {
            Assertions.assertThat(repository.findAll()).isEmpty();
        }
    }

}