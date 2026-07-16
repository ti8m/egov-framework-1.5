package ch.ti8m.egov.framework.validation.engine;

import ch.ti8m.egov.testbase.TestApplicationContext;
import ch.ti8m.egov.testbase.entities.relationship.TestEntity;
import ch.ti8m.egov.testbase.entities.relationship.TestSubEntityManyToOne;
import ch.ti8m.egov.testbase.entities.relationship.TestSubEntityOneToOneMapsId;
import ch.ti8m.egov.testbase.repositories.TestEntityRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
@Transactional
@SpringBootTest
@ActiveProfiles({"test", "test-logging-debug"})
public class FieldValueExtractorComponentTest extends TestApplicationContext {

    @Autowired
    TestEntityRepository testEntityRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    FieldValueExtractorComponent fieldValueExtractorComponent;

    @Test
    void getDeclaredField_objectIsManyToOneHibernateProxy_initializesEntityAndReturnsTheFieldValue() {

        var testEntity = new TestEntity();

        var testSubEntityManyToOne = new TestSubEntityManyToOne();
        testSubEntityManyToOne.setContent("Test");
        testEntity.setTestSubEntityManyToOne(testSubEntityManyToOne);

        testEntityRepository.save(testEntity);

        entityManager.flush();
        entityManager.clear();

        TestEntity testEntityFromDb = testEntityRepository.findById(testEntity.getId()).orElseThrow();

        final TestSubEntityManyToOne testSubEntityManyToOneFromDb = testEntityFromDb.getTestSubEntityManyToOne();

        assertThat(testSubEntityManyToOneFromDb).isInstanceOf(HibernateProxy.class);

        // act
        final Object content = fieldValueExtractorComponent.getDeclaredField("content", testSubEntityManyToOneFromDb, "root.content");

        assertThat(content).isEqualTo("Test");
    }


    @Test
    void getDeclaredField_objectIsOneToOneHibernateProxy_initializesEntityAndReturnsTheFieldValue() {

        var testEntity = new TestEntity();
        testEntity.setSomeValue("Test");

        var testSubEntityOneToOneMapsId = new TestSubEntityOneToOneMapsId();
        testSubEntityOneToOneMapsId.setTestEntity(testEntity);

        entityManager.persist(testSubEntityOneToOneMapsId);
        entityManager.flush();
        entityManager.clear();

        var testSubEntityOneToOneMapsIdFromDb = entityManager.find(TestSubEntityOneToOneMapsId.class, testSubEntityOneToOneMapsId.getId());

        var testEntityFromDb = testSubEntityOneToOneMapsIdFromDb.getTestEntity();

        assertThat(testEntityFromDb).isInstanceOf(HibernateProxy.class);

        // act
        final Object content = fieldValueExtractorComponent.getDeclaredField("someValue", testEntityFromDb, "root.someValue");

        assertThat(content).isEqualTo("Test");
    }

    @Test
    void getDeclaredField_objectIsOneToOneHibernateProxyButMissingAssociation_throwsAndCatchesEntityNotFoundAndReturnsNull(CapturedOutput capturedOutput) {

        var testEntity = new TestEntity();
        testEntity.setSomeValue("Test");

        var testSubEntityOneToOneMapsId = new TestSubEntityOneToOneMapsId();
        testSubEntityOneToOneMapsId.setTestEntity(testEntity);

        entityManager.persist(testSubEntityOneToOneMapsId);
        entityManager.flush();
        entityManager.clear();
        TestEntity testEntityFromDbToDelete = testEntityRepository.findById(testEntity.getId()).orElseThrow();
        testEntityRepository.delete(testEntityFromDbToDelete);
        entityManager.flush();
        entityManager.clear();
        final TestSubEntityOneToOneMapsId testSubEntityOneToOneMapsIdFromDb = entityManager.find(TestSubEntityOneToOneMapsId.class, testSubEntityOneToOneMapsId.getId());

        final TestEntity testEntityFromDb = testSubEntityOneToOneMapsIdFromDb.getTestEntity();

        assertThat(testEntityFromDb).isInstanceOf(HibernateProxy.class);

        // act
        final Object value = fieldValueExtractorComponent.getDeclaredField("someValue", testEntityFromDb, "root.someValue");

        assertThat(value).isNull();
        assertThat(capturedOutput.getOut()).contains("Unable to find " + TestEntity.class.getName() + " with id " + testEntity.getId());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void getDeclaredField_noSessionDuringHibernateInitialization_throwsLazyInitializationException() {
        var testEntity = new TestEntity();

        var testSubEntityManyToOne = new TestSubEntityManyToOne();
        testSubEntityManyToOne.setContent("Test");
        testEntity.setTestSubEntityManyToOne(testSubEntityManyToOne);

        testEntityRepository.save(testEntity);
        entityManager.clear();

        TestEntity testEntityFromDb = testEntityRepository.findById(testEntity.getId()).orElseThrow();

        final TestSubEntityManyToOne testSubEntityManyToOneFromDb = testEntityFromDb.getTestSubEntityManyToOne();

        assertThat(testSubEntityManyToOneFromDb).isInstanceOf(HibernateProxy.class);

        // act
        Assertions.assertThatThrownBy(() -> fieldValueExtractorComponent.getDeclaredField("content", testSubEntityManyToOneFromDb, "root.content"))
                .isExactlyInstanceOf(ValidationException.class)
                .hasMessageContaining("Cause is LazyInitializationException.");

        testEntityRepository.delete(testEntity.getId());
    }

    @Test
    void getDeclaredField_unknownExceptionDuringHibernateInitialization_throwsTheUnknownException() {
        var testEntity = new TestEntity();

        var testSubEntityManyToOne = new TestSubEntityManyToOne();
        testEntity.setTestSubEntityManyToOne(testSubEntityManyToOne);

        testEntityRepository.save(testEntity);
        entityManager.clear();

        TestEntity testEntityFromDb = testEntityRepository.findById(testEntity.getId()).orElseThrow();

        final TestSubEntityManyToOne testSubEntityManyToOneFromDb = testEntityFromDb.getTestSubEntityManyToOne();

        assertThat(testSubEntityManyToOneFromDb).isInstanceOf(HibernateProxy.class);

        try (final MockedStatic<Hibernate> hibernateMockedStatic = Mockito.mockStatic(Hibernate.class)) {
            hibernateMockedStatic.when(() -> Hibernate.initialize(Mockito.any())).thenThrow(new RuntimeException("TEST_EXCEPTION"));

            // act
            Assertions.assertThatThrownBy(() -> fieldValueExtractorComponent.getDeclaredField("content", testSubEntityManyToOneFromDb, "root.content"))
                    .isExactlyInstanceOf(ValidationException.class)
                    .hasMessageContaining("TEST_EXCEPTION");
        }
    }
}
