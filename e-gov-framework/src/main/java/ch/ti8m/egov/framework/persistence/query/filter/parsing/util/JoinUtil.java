package ch.ti8m.egov.framework.persistence.query.filter.parsing.util;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.iam.components.ClassUtilityComponent;
import ch.ti8m.egov.framework.persistence.base.ArchivedModifiableEntity;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.persistence.query.JoinConfiguration;
import ch.ti8m.egov.framework.persistence.query.QueryBuilder;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.column.TableColumn;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.join.LeftJoin;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.join.TableJoin;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.join.TableJoins;
import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import ch.ti8m.egov.framework.persistence.util.ReflectionUtils;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

@Slf4j
public final class JoinUtil {

    private JoinUtil() {
    }

    public static Pair<String, TableJoins> getJoin(
            final Class<? extends ModifiableEntity> clazz,
            final Queue<String> pathSegments,
            final TableJoins joins,
            final NameTranslationComponent nameTranslationComponent,
            final boolean isRootJoin,
            final JoinConfiguration joinConfiguration) {
        final String queueEntry = pathSegments.poll();
        if (pathSegments.isEmpty()) {
            final Field field = FieldUtils.getField(clazz, queueEntry, true);
            if (field != null) {
                final String fieldName = getTableName(clazz, isRootJoin, nameTranslationComponent) + "." + nameTranslationComponent.getTranslatedColumnName(field);
                return Pair.of(fieldName, joins);
            } else if (joinConfiguration.isFieldNameToBeDatabaseColumnNameAllowed()) {
                log.warn("Field {} does not exist on class {}. Using it directly as database column name. This will be disabled in future versions.", queueEntry, clazz.getSimpleName());
                final String fieldName = getTableName(clazz, isRootJoin, nameTranslationComponent) + "." + queueEntry;
                return Pair.of(fieldName, joins);
            } else {
                throw new EGovException(ExceptionCode.FIELD_NOT_FOUND_IN_CLASS, "Field " + queueEntry + " does not exist on class: " + clazz.getSimpleName());
            }
        }
        return getJoin(
                getFieldType(clazz, queueEntry, joins, nameTranslationComponent, isRootJoin, joinConfiguration),
                pathSegments,
                joins,
                nameTranslationComponent,
                false,
                joinConfiguration);
    }

    private static Class<? extends ModifiableEntity> getFieldType(
            final Class<? extends ModifiableEntity> clazz,
            final String requestField,
            final Map<String, TableJoin> joins,
            final NameTranslationComponent nameTranslationComponent,
            final boolean isRootJoin,
            final JoinConfiguration joinConfiguration) {
        for (final Field field : ReflectionUtils.getAllEntityFields(clazz)) {
            if (field.getName().equals(requestField)) {
                if (field.isAnnotationPresent(OneToMany.class) && !joinConfiguration.isOneToManyJoinAllowed()) {
                    throw new EGovException(ExceptionCode.SORT_NOT_ALLOWED, "Join requested for oneToMany relation which is not allowed in this context.");
                } else if ((field.isAnnotationPresent(Column.class)
                        || field.isAnnotationPresent(ManyToOne.class)
                        || field.isAnnotationPresent(OneToMany.class)
                        || field.isAnnotationPresent(OneToOne.class))) {
                    final Class<? extends ModifiableEntity> owningClazz = castToModifiableEntity(field.getDeclaringClass());
                    final String clazzTableName = getTableName(owningClazz, isRootJoin, nameTranslationComponent);
                    final String clazzJoinColumn;
                    final Class<? extends ModifiableEntity> nextClazz;
                    final String nextClazzTableName;
                    final String nextClazzJoinColumn;
                    if (field.getType().isAssignableFrom(List.class)) {
                        final ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                        nextClazz = castToModifiableEntity(stringListType.getActualTypeArguments()[0]);
                        final JoinColumn joinColumn = field.isAnnotationPresent(JoinColumn.class)
                                ? field.getAnnotation(JoinColumn.class)
                                : getNextClazzJoinField(owningClazz, nextClazz).getAnnotation(JoinColumn.class);
                        clazzJoinColumn = getJoinColumnName(joinColumn, nameTranslationComponent);
                        nextClazzJoinColumn = joinColumn.name();
                        nextClazzTableName = getTableName(nextClazz, false, nameTranslationComponent);
                    } else {
                        nextClazz = castToModifiableEntity(field.getType());
                        final JoinColumn joinColumn;
                        if (field.isAnnotationPresent(OneToOne.class)) {
                            if (!field.getAnnotation(OneToOne.class).mappedBy().isEmpty()) {
                                joinColumn = getNextClazzJoinField(owningClazz, nextClazz).getAnnotation(JoinColumn.class);
                                nextClazzTableName = getTableName(nextClazz, false, nameTranslationComponent);
                                clazzJoinColumn = getJoinColumnName(joinColumn, owningClazz, nameTranslationComponent);
                                nextClazzJoinColumn = joinColumn.name();
                            } else {
                                joinColumn = field.getAnnotation(JoinColumn.class);
                                nextClazzTableName = getTableName(nextClazz, false, nameTranslationComponent);
                                clazzJoinColumn = joinColumn.name();
                                nextClazzJoinColumn = getJoinColumnName(joinColumn, nameTranslationComponent);
                            }
                        } else {
                            joinColumn = field.getAnnotation(JoinColumn.class);
                            nextClazzTableName = getTableName(nextClazz, false, nameTranslationComponent);
                            clazzJoinColumn = joinColumn.name();
                            nextClazzJoinColumn = getJoinColumnName(joinColumn, nameTranslationComponent);
                        }
                    }

                    final TableColumn tableJoinColumn = new TableColumn(clazzTableName, clazzJoinColumn);
                    final TableColumn joinTableJoinColumn = new TableColumn(nextClazzTableName, nextClazzJoinColumn);

                    final LeftJoin leftJoin = new LeftJoin(
                            nextClazzTableName,
                            tableJoinColumn,
                            joinTableJoinColumn,
                            isArchivedModifiableEntity(nextClazz),
                            joinConfiguration);

                    joins.put(nextClazzTableName, leftJoin);

                    return nextClazz;
                }
            }
        }
        throw new EGovException(ExceptionCode.COMPILER_ERROR, "field is unknown or not an entity column: " + requestField);
    }

    private static String getJoinColumnName(final JoinColumn joinColumn, final NameTranslationComponent nameTranslationComponent) {
        return getJoinColumnName(joinColumn, null, nameTranslationComponent);
    }

    private static String getJoinColumnName(final JoinColumn joinColumn, final Class<?> clazz, final NameTranslationComponent nameTranslationComponent) {
        return joinColumn.referencedColumnName() == null || joinColumn.referencedColumnName().isEmpty()
                ? clazz == null ? "id" : ClassUtilityComponent.getIdFieldName(clazz) // todo: use default database join column, see hibernate implementation for reference
                : nameTranslationComponent.getTranslatedName(joinColumn.referencedColumnName());
    }

    private static Field getNextClazzJoinField(final Class<? extends ModifiableEntity> clazz,
                                               final Class<? extends ModifiableEntity> nextClazz) {
        for (final Field joinEntityClassField : ReflectionUtils.getAllEntityFields(nextClazz)) {
            if (joinEntityClassField.isAnnotationPresent(JoinColumn.class) && clazz.isAssignableFrom(joinEntityClassField.getType())) {
                return joinEntityClassField;
            }
        }
        throw new EGovException(ExceptionCode.COMPILER_ERROR, "JoinColumn for " + nextClazz.getSimpleName() + " not found on " + clazz.getSimpleName());
    }

    public static String toJoinStatements(final Collection<TableJoin> tableJoins) {
        return tableJoins
                .stream()
                .map(TableJoin::toString)
                .collect(Collectors.joining());
    }

    private static boolean isArchivedModifiableEntity(final Class<? extends ModifiableEntity> clazz) {
        return ArchivedModifiableEntity.class.isAssignableFrom(clazz);
    }

    private static String getTableName(
            final Class<? extends ModifiableEntity> clazz,
            final boolean isRootJoin,
            final NameTranslationComponent nameTranslationComponent
    ) {
        return isRootJoin
                ? QueryBuilder.ROOT_TABLE_ALIAS
                : nameTranslationComponent.getTranslatedEntityName(clazz);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends ModifiableEntity> castToModifiableEntity(final Type clazz) {
        return (Class<? extends ModifiableEntity>) clazz;
    }


}
