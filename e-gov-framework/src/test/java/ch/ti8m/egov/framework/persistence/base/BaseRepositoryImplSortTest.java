package ch.ti8m.egov.framework.persistence.base;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.testbase.entities.relationship.TestEntity;
import ch.ti8m.egov.testbase.entities.relationship.TestSubEntityOneToMany;
import ch.ti8m.egov.testbase.repositories.TestSubEntityOneToManyRepository;
import ch.ti8m.egov.testbase.tools.RepositoryTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ch.ti8m.egov.testbase.tools.EntityFactory.newMoritzEntity;

@ActiveProfiles("test-sqlserver")
class SQLServerBaseRepositoryImplSortTest extends BaseRepositoryImplSortTest {
}

@ActiveProfiles("test-postgres")
class PostgresBaseRepositoryImplSortTest extends BaseRepositoryImplSortTest {
}

abstract class BaseRepositoryImplSortTest extends RepositoryTestBase {

    @SpyBean
    protected TestSubEntityOneToManyRepository testSubEntityOneToManyRepository;

    @Test
    void sortOnBasicAttribute() {
        repository.deactivatePermissions();

        final TestEntity firstEntity = newMoritzEntity();
        firstEntity.setLastname("a");
        final TestEntity secondEntity = newMoritzEntity();
        secondEntity.setLastname("b");
        final TestEntity thirdEntity = newMoritzEntity();
        thirdEntity.setLastname("c");

        repository.saveWithTx(secondEntity);
        repository.saveWithTx(thirdEntity);
        repository.saveWithTx(firstEntity);

        final List<TestEntity> retrievedEntities = repository.findAll(Sorting.builder()
                .field(TestEntity.Fields.lastname)
                .ascending()
                .get());

        Assertions.assertThat(retrievedEntities.stream().map(TestEntity::getId).toList())
                .containsExactly(firstEntity.getId(), secondEntity.getId(), thirdEntity.getId());
    }

    @Test
    void sortOnMultipleBasicAttributes() {
        repository.deactivatePermissions();

        final TestEntity firstEntity = newMoritzEntity();
        firstEntity.setLastname("a");
        firstEntity.setFirstname("a");
        final TestEntity secondEntity = newMoritzEntity();
        secondEntity.setLastname("a");
        secondEntity.setFirstname("b");
        final TestEntity thirdEntity = newMoritzEntity();
        thirdEntity.setLastname("b");

        repository.saveWithTx(secondEntity);
        repository.saveWithTx(thirdEntity);
        repository.saveWithTx(firstEntity);

        final List<TestEntity> retrievedEntities = repository.findAll(Sorting.builder()
                .field(TestEntity.Fields.lastname).ascending()
                .and()
                .field(TestEntity.Fields.firstname).ascending()
                .get());

        Assertions.assertThat(retrievedEntities.stream().map(TestEntity::getId).toList()).containsExactly(
                firstEntity.getId(),
                secondEntity.getId(),
                thirdEntity.getId()
        );
    }

    @Test
    void exceptionOnSortOnAttributeOnSubEntityOneToMany() {
        repository.deactivatePermissions();

        final TestEntity firstEntity = newMoritzEntity();
        firstEntity.getTestSubEntitiesOneToMany().get(0).setContent("a");
        final TestEntity secondEntity = newMoritzEntity();
        secondEntity.getTestSubEntitiesOneToMany().get(0).setContent("b");
        final TestEntity thirdEntity = newMoritzEntity();
        thirdEntity.getTestSubEntitiesOneToMany().get(0).setContent("c");

        repository.saveWithTx(secondEntity);
        repository.saveWithTx(thirdEntity);
        repository.saveWithTx(firstEntity);
        org.junit.jupiter.api.Assertions.assertThrows(
                EGovException.class,
                () -> repository.findAll(Sorting.builder()
                        .field(TestEntity.Fields.testSubEntitiesOneToMany + "." + TestSubEntityOneToMany.Fields.content)
                        .ascending()
                        .get()),
                "Expected exception on OneToMany Join not thrown."
        );
    }

    @Test
    void sortOnSubEntityManyToOneDescending() {
        repository.deactivatePermissions();
        testSubEntityOneToManyRepository.deactivatePermissions();

        final TestEntity firstEntity = newMoritzEntity();
        final TestSubEntityOneToMany firstTestSubEntityOneToMany = firstEntity.getTestSubEntitiesOneToMany().get(0);
        firstEntity.setTestSubEntitiesOneToMany(List.of(firstTestSubEntityOneToMany));
        firstEntity.setLastname("a");
        repository.saveWithTx(firstEntity);
        final TestEntity secondEntity = newMoritzEntity();
        final TestSubEntityOneToMany secondTestSubEntityOneToMany = secondEntity.getTestSubEntitiesOneToMany().get(0);
        secondEntity.setTestSubEntitiesOneToMany(List.of(secondTestSubEntityOneToMany));
        secondEntity.setLastname("b");
        repository.saveWithTx(secondEntity);

        final List<TestSubEntityOneToMany> retrievedEntities = testSubEntityOneToManyRepository.findAll(
                Sorting.builder()
                        .field(TestSubEntityOneToMany.Fields.testEntity + "." + TestEntity.Fields.lastname)
                        .descending()
                        .get()
        );

        Assertions.assertThat(retrievedEntities.stream().map(TestSubEntityOneToMany::getId).toList()).containsExactly(
                secondTestSubEntityOneToMany.getId(),
                firstTestSubEntityOneToMany.getId()
        );
    }

    @Test
    void sortOnSubEntityManyToOneAscending() {
        repository.deactivatePermissions();
        testSubEntityOneToManyRepository.deactivatePermissions();

        final TestEntity firstEntity = newMoritzEntity();
        final TestSubEntityOneToMany firstTestSubEntityOneToMany = firstEntity.getTestSubEntitiesOneToMany().get(0);
        firstEntity.setTestSubEntitiesOneToMany(List.of(firstTestSubEntityOneToMany));
        firstEntity.setLastname("a");
        repository.saveWithTx(firstEntity);
        final TestEntity secondEntity = newMoritzEntity();
        final TestSubEntityOneToMany secondTestSubEntityOneToMany = secondEntity.getTestSubEntitiesOneToMany().get(0);
        secondEntity.setTestSubEntitiesOneToMany(List.of(secondTestSubEntityOneToMany));
        secondEntity.setLastname("b");
        repository.saveWithTx(secondEntity);

        final List<TestSubEntityOneToMany> retrievedEntities = testSubEntityOneToManyRepository.findAll(
                Sorting.builder()
                        .field(TestSubEntityOneToMany.Fields.testEntity + "." + TestEntity.Fields.lastname)
                        .ascending()
                        .get()
        );

        Assertions.assertThat(retrievedEntities.stream().map(TestSubEntityOneToMany::getId).toList()).containsExactly(
                firstTestSubEntityOneToMany.getId(),
                secondTestSubEntityOneToMany.getId()
        );
    }

    @Test
    void sortOnSubEntityCorrectCount() {
        repository.deactivatePermissions();
        testSubEntityOneToManyRepository.deactivatePermissions();
        Mockito.when(testSubEntityOneToManyRepository.isPrimary()).thenReturn(true);

        final TestEntity firstEntity = newMoritzEntity();
        final TestSubEntityOneToMany firstTestSubEntityOneToMany = firstEntity.getTestSubEntitiesOneToMany().get(0);
        firstEntity.setTestSubEntitiesOneToMany(List.of(firstTestSubEntityOneToMany));
        firstEntity.setLastname("a");
        repository.saveWithTx(firstEntity);
        final TestEntity secondEntity = newMoritzEntity();
        final TestSubEntityOneToMany secondTestSubEntityOneToMany = secondEntity.getTestSubEntitiesOneToMany().get(0);
        secondEntity.setTestSubEntitiesOneToMany(List.of(secondTestSubEntityOneToMany));
        secondEntity.setLastname("b");
        repository.saveWithTx(secondEntity);

        testSubEntityOneToManyRepository.findAll(
                Sorting.builder()
                        .field(TestSubEntityOneToMany.Fields.testEntity + "." + TestEntity.Fields.lastname)
                        .ascending()
                        .get()
        );

        Assertions.assertThat(DataHolder.getCount()).isEqualTo(2);
    }

    @Test
    @Transactional
    void sortOnAttributeOnSubEntityManyToOne() {
        repository.deactivatePermissions();
        testSubEntityOneToManyRepository.deactivatePermissions();

        final TestEntity firstEntity = newMoritzEntity();
        firstEntity.setLastname("a");
        final TestEntity secondEntity = newMoritzEntity();
        secondEntity.setLastname("c");
        final TestEntity thirdEntity = newMoritzEntity();
        thirdEntity.setLastname("b");

        repository.saveWithTx(secondEntity);
        repository.saveWithTx(thirdEntity);
        repository.saveWithTx(firstEntity);

        final List<TestSubEntityOneToMany> retrievedEntities = testSubEntityOneToManyRepository.findAll(Sorting.builder()
                .field(TestSubEntityOneToMany.Fields.testEntity + "." + TestEntity.Fields.lastname)
                .ascending()
                .get());

        Assertions.assertThat(retrievedEntities.stream().map(TestSubEntityOneToMany::getTestEntity).map(TestEntity::getId).toList()).containsExactly(
                firstEntity.getId(),
                firstEntity.getId(),
                firstEntity.getId(),
                thirdEntity.getId(),
                thirdEntity.getId(),
                thirdEntity.getId(),
                secondEntity.getId(),
                secondEntity.getId(),
                secondEntity.getId()
        );
    }

    @Test
    void sortOnAttributeOnSubEntityManyToOneMultipleSorts() {
        repository.deactivatePermissions();
        testSubEntityOneToManyRepository.deactivatePermissions();

        final TestEntity firstEntity = newMoritzEntity();
        firstEntity.setLastname("a");
        firstEntity.setFirstname("a");
        final TestEntity secondEntity = newMoritzEntity();
        secondEntity.setLastname("b");
        final TestEntity thirdEntity = newMoritzEntity();
        thirdEntity.setLastname("a");
        thirdEntity.setFirstname("b");

        repository.saveWithTx(secondEntity);
        repository.saveWithTx(thirdEntity);
        repository.saveWithTx(firstEntity);

        final List<TestSubEntityOneToMany> retrievedEntities = testSubEntityOneToManyRepository.findAll(Sorting.builder()
                .field(TestSubEntityOneToMany.Fields.testEntity + "." + TestEntity.Fields.lastname)
                .ascending()
                .and()
                .field(TestSubEntityOneToMany.Fields.testEntity + "." + TestEntity.Fields.firstname)
                .ascending()
                .get());

        Assertions.assertThat(retrievedEntities.stream().map(TestSubEntityOneToMany::getTestEntity).map(TestEntity::getId).toList()).containsExactly(
                firstEntity.getId(),
                firstEntity.getId(),
                firstEntity.getId(),
                thirdEntity.getId(),
                thirdEntity.getId(),
                thirdEntity.getId(),
                secondEntity.getId(),
                secondEntity.getId(),
                secondEntity.getId()
        );
    }

}
