package ch.ti8m.egov.framework.persistence.query.filter.parsing.util;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.persistence.query.JoinConfiguration;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.join.TableJoins;
import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JoinUtilTest {

    JoinConfiguration defaultJoinConfiguration = new JoinConfiguration(true, true, true, "FalseStatement");
    @Mock
    private NameTranslationComponent nameTranslationComponent;

    @Test
    void noJoinIfFieldOnRootEntity() {
        when(nameTranslationComponent.getTranslatedColumnName(Mockito.any(Field.class))).thenAnswer(invocationOnMock -> {
            final Field field = invocationOnMock.getArgument(0);
            return field.getName();
        });

        final Pair<String, TableJoins> join = JoinUtil.getJoin(
                Aggregate.class,
                new LinkedList<>(List.of("name")),
                new TableJoins(),
                nameTranslationComponent,
                true,
                defaultJoinConfiguration);
        assertThat(join.getLeft()).isEqualTo("rootTable.name");
        assertThat(join.getRight()).isEmpty();
    }

    @Test
    void simpleJoinOnManyToOneRelationWithImplicitReferencedColumn() {
        when(nameTranslationComponent.getTranslatedColumnName(Mockito.any(Field.class))).thenAnswer(invocationOnMock -> {
            final Field field = invocationOnMock.getArgument(0);
            return field.getName();
        });
        when(nameTranslationComponent.getTranslatedEntityName(Mockito.any(Class.class))).thenAnswer(invocationOnMock -> {
            final Class<?> clazz = invocationOnMock.getArgument(0);
            return clazz.getSimpleName();
        });

        final Pair<String, TableJoins> join = JoinUtil.getJoin(
                Aggregate.class,
                new LinkedList<>(Arrays.asList("manyToOneSubEntity.subEntityName".split("\\."))),
                new TableJoins(),
                nameTranslationComponent,
                true,
                defaultJoinConfiguration);
        assertThat(join.getLeft()).isEqualTo("ManyToOneSubEntity.subEntityName");
        assertThat(join.getRight().get("ManyToOneSubEntity")).hasToString(" LEFT JOIN ManyToOneSubEntity ON ManyToOneSubEntity.id = rootTable.aggregateId_FK");
    }

    @Test
    void simpleJoinOnManyToOneRelationWithExplicitReferencedColumn() {
        when(nameTranslationComponent.getTranslatedColumnName(Mockito.any(Field.class))).thenAnswer(invocationOnMock -> {
            final Field field = invocationOnMock.getArgument(0);
            return field.getName();
        });
        when(nameTranslationComponent.getTranslatedEntityName(Mockito.any(Class.class))).thenAnswer(invocationOnMock -> {
            final Class<?> clazz = invocationOnMock.getArgument(0);
            return clazz.getSimpleName();
        });
        when(nameTranslationComponent.getTranslatedName(Mockito.anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        final Pair<String, TableJoins> join = JoinUtil.getJoin(
                Aggregate.class,
                new LinkedList<>(Arrays.asList("manyToOneSubEntityExplicit.subEntityName".split("\\."))),
                new TableJoins(),
                nameTranslationComponent,
                true,
                defaultJoinConfiguration);

        assertThat(join.getLeft()).isEqualTo("ManyToOneSubEntity.subEntityName");
        assertThat(join.getRight().get("ManyToOneSubEntity")).hasToString(" LEFT JOIN ManyToOneSubEntity ON ManyToOneSubEntity.aggregateId = rootTable.aggregateId_FK");
    }

    @Test
    void simpleJoinOnOneToManyRelationWithImplicitReferencedColumn() {
        when(nameTranslationComponent.getTranslatedColumnName(Mockito.any(Field.class))).thenAnswer(invocationOnMock -> {
            final Field field = invocationOnMock.getArgument(0);
            return field.getName();
        });
        when(nameTranslationComponent.getTranslatedEntityName(Mockito.any(Class.class))).thenAnswer(invocationOnMock -> {
            final Class<?> clazz = invocationOnMock.getArgument(0);
            return clazz.getSimpleName();
        });

        final Pair<String, TableJoins> join = JoinUtil.getJoin(
                Aggregate.class,
                new LinkedList<>(Arrays.asList("oneToManySubEntitiesImplicit.subEntityTitle".split("\\."))),
                new TableJoins(),
                nameTranslationComponent,
                true,
                defaultJoinConfiguration);

        assertThat(join.getLeft()).isEqualTo("OneToManySubEntity.subEntityTitle");
        assertThat(join.getRight().get("OneToManySubEntity")).hasToString(" LEFT JOIN OneToManySubEntity ON OneToManySubEntity.aggregateId_FK = rootTable.id");
    }

    @Test
    void exceptionOnOneToManyJoinIfItsNotAllowed() {
        LinkedList<String> pathSegments = new LinkedList<>(Arrays.asList("oneToManySubEntitiesImplicit.subEntityTitle".split("\\.")));
        TableJoins joins = new TableJoins();
        JoinConfiguration joinConfiguration = new JoinConfiguration(
                true,
                false,
                true,
                "FalseStatement");

        assertThrows(EGovException.class,
                () -> JoinUtil.getJoin(
                        Aggregate.class,
                        pathSegments,
                        joins,
                        nameTranslationComponent,
                        true,
                        joinConfiguration),
                "Expected exception on OneToMany Join not thrown."
        );
    }

    @Test
    void simpleJoinOnOneToManyRelationWithExplicitReferencedColumn() {
        when(nameTranslationComponent.getTranslatedColumnName(Mockito.any(Field.class))).thenAnswer(invocationOnMock -> {
            final Field field = invocationOnMock.getArgument(0);
            return field.getName();
        });
        when(nameTranslationComponent.getTranslatedEntityName(Mockito.any(Class.class))).thenAnswer(invocationOnMock -> {
            final Class<?> clazz = invocationOnMock.getArgument(0);
            return clazz.getSimpleName();
        });
        when(nameTranslationComponent.getTranslatedName(Mockito.anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        final Pair<String, TableJoins> join = JoinUtil.getJoin(
                Aggregate.class,
                new LinkedList<>(Arrays.asList("oneToManySubEntitiesExplicit.subEntityTitle".split("\\."))),
                new TableJoins(),
                nameTranslationComponent,
                true,
                defaultJoinConfiguration);

        assertThat(join.getLeft()).isEqualTo("OneToManySubEntityExplicit.subEntityTitle");
        assertThat(join.getRight().get("OneToManySubEntityExplicit")).hasToString(" LEFT JOIN OneToManySubEntityExplicit ON OneToManySubEntityExplicit.aggregateId_FK = rootTable.aggregateId");
    }

    @Test
    void joinWithNoBackReference() {
        when(nameTranslationComponent.getTranslatedColumnName(Mockito.any(Field.class))).thenAnswer(invocationOnMock -> {
            final Field field = invocationOnMock.getArgument(0);
            return field.getName();
        });
        when(nameTranslationComponent.getTranslatedEntityName(Mockito.any(Class.class))).thenAnswer(invocationOnMock -> {
            final Class<?> clazz = invocationOnMock.getArgument(0);
            return clazz.getSimpleName();
        });
        when(nameTranslationComponent.getTranslatedName(Mockito.anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        final Pair<String, TableJoins> join = JoinUtil.getJoin(
                Aggregate.class,
                new LinkedList<>(Arrays.asList("oneToManyWithoutBackReferences.subEntityTitle".split("\\."))),
                new TableJoins(),
                nameTranslationComponent,
                true,
                defaultJoinConfiguration);

        assertThat(join.getLeft()).isEqualTo("OneToManyWithoutBackReference.subEntityTitle");
        assertThat(join.getRight().get("OneToManyWithoutBackReference")).hasToString(" LEFT JOIN OneToManyWithoutBackReference ON OneToManyWithoutBackReference.aggregateFk = rootTable.aggregateId");
    }

    private class Aggregate extends ModifiableEntity {

        private String name;
        private Long aggregateId;

        @ManyToOne
        @JoinColumn(name = "aggregateId_FK")
        private ManyToOneSubEntity manyToOneSubEntity;

        @ManyToOne
        @JoinColumn(name = "aggregateId_FK", referencedColumnName = "aggregateId")
        private ManyToOneSubEntity manyToOneSubEntityExplicit;

        @OneToMany(mappedBy = "aggregateImplicit")
        private List<OneToManySubEntity> oneToManySubEntitiesImplicit;

        @OneToMany(mappedBy = "aggregateExplicit")
        private List<OneToManySubEntityExplicit> oneToManySubEntitiesExplicit;

        @OneToMany
        @JoinColumn(name = "aggregateFk", referencedColumnName = "aggregateId")
        private List<OneToManyWithoutBackReference> oneToManyWithoutBackReferences;

    }

    private class ManyToOneSubEntity extends ModifiableEntity {

        private String subEntityName;

    }

    private class OneToManySubEntity extends ModifiableEntity {

        private String subEntityTitle;

        @ManyToOne
        @JoinColumn(name = "aggregateId_FK")
        private Aggregate aggregateImplicit;

    }

    private class OneToManySubEntityExplicit extends ModifiableEntity {

        private String subEntityTitle;

        @ManyToOne
        @JoinColumn(name = "aggregateId_FK", referencedColumnName = "aggregateId")
        private Aggregate aggregateExplicit;

    }

    private class OneToManyWithoutBackReference {

        private Long aggregateFk;
        private String subEntityTitle;

    }

}
