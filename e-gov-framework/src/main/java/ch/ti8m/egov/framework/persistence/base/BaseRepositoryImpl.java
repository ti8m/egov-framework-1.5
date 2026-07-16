package ch.ti8m.egov.framework.persistence.base;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.iam.api.model.PermissionOperation;
import ch.ti8m.egov.framework.persistence.query.EntityMetadata;
import ch.ti8m.egov.framework.persistence.query.ParametrizedQuery;
import ch.ti8m.egov.framework.persistence.query.QueryBuilder;
import ch.ti8m.egov.framework.persistence.query.QueryConfig;
import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import ch.ti8m.egov.framework.persistence.util.ReflectionUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.Id;
import jakarta.persistence.Query;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.Filter;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Getter
@Component
public abstract class BaseRepositoryImpl<T extends ModifiableEntity> {

    public static final String DEFAULT_ORDER_BY = Sorting.builder()
            .field(ModifiableEntity.Fields.modifiedDate)
            .descending()
            .get();

    @Autowired
    private QueryBuilder queryBuilder;
    @Autowired
    private NameTranslationComponent nameTranslationComponent;
    @Autowired
    private GlobalRepositoryConfigurationService globalRepositoryConfigurationService;
    @Autowired
    private EntityManagerProvider entityManagerProvider;

    public static String sqlSearchValueOf(final String value) {
        return "%" + value + "%";
    }

    public EntityManager getEntityManager() {
        return this.entityManagerProvider.getEntityManager();
    }

    protected boolean isPrimary() {
        try {
            for (final StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                if (stackTraceElement.getClassName().startsWith("jdk.internal.reflect")) {
                    // skipping JVM generated accessor classes as they cannot be loaded
                    continue;
                }
                final Class<?> clazz = ReflectionUtils.fromClassName(stackTraceElement.getClassName(), this.getClass().getClassLoader());

                for (final Field field : clazz.getDeclaredFields()) {
                    if (field.getType().equals(this.getClass())) {
                        return field.isAnnotationPresent(PrimaryRepository.class);
                    }
                }
            }
        } catch (final ClassNotFoundException e) {
            BaseRepositoryImpl.log.warn("Could not calculate if the repository {} is primary.", this.getClass().getName());
            return false;
        }
        return false;
    }

    // SAVE
    @Transactional
    public void save(final T entity) {
        if (getId(entity) != null) {
            throw new EGovException(ExceptionCode.ILLEGAL_USE_OF_SAVE, "Illegal use of save");
        }
        getEntityManager().persist(entity);
        DataHolder.getRepositoryInstanceContext(this.toString()).getEntityIds().add(getId(entity));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long saveWithTx(final T entity) {
        save(entity);
        return getId(entity);
    }

    @Transactional
    public void save(final List<T> entities) {
        for (final T entity : entities) {
            if (getId(entity) != null) {
                throw new EGovException(ExceptionCode.ILLEGAL_USE_OF_SAVE, "Illegal use of save");
            }
            save(entity);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveWithTx(final List<T> entities) {
        save(entities);
    }

    // UPDATE
    @Transactional
    public T update(final T entity) {
        // todo: need to switch to findById(id, PermissionOperation.DELETE, false)
        // careful: needs to be false, as deleted entities may not be deleted again
        if (userCanExecuteOperation(getId(entity))) {
            return getEntityManager().merge(entity);
        } else {
            throw new EGovException(ExceptionCode.UPDATE_FORBIDDEN, "Update forbidden");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateWithTx(final T entity) {
        update(entity);
    }

    @Transactional
    public void update(final List<T> entities) {
        for (final T entity : entities) {
            update(entity);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateWithTx(final List<T> entities) {
        update(entities);
    }

    // DELETE
    @Transactional
    public void delete(final Long id) {
        findById(id, PermissionOperation.DELETE, true).ifPresent(getEntityManager()::remove);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteWithTx(final Long id) {
        findById(id, PermissionOperation.DELETE, true).ifPresent(getEntityManager()::remove);
    }

    @Deprecated(forRemoval = true) // todo: back compatability for curiaplus
    @Transactional
    public void delete(final Integer id) {
        findById(id.longValue(), PermissionOperation.DELETE, true).ifPresent(getEntityManager()::remove);
    }

    @Deprecated(forRemoval = true) // todo: back compatability for curiaplus
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteWithTx(final Integer id) {
        findById(id.longValue(), PermissionOperation.DELETE, true).ifPresent(getEntityManager()::remove);
    }

    @Transactional
    public void delete(final List<T> entities) {
        for (final T entity : entities) {
            delete(getId(entity));
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteWithTx(final List<T> entities) {
        delete(entities);
    }

    @Transactional
    public void delete(final T entity) {
        delete(getId(entity));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteWithTx(final T entity) {
        delete(getId(entity));
    }

    @Transactional
    public void deleteAll() {
        forceFindAll().forEach(entity -> delete(getId(entity)));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAllWithTx() {
        deleteAll();
    }

    // Modification checks
    public boolean canUser(final PermissionOperation operation, final Long id) {
        return findById(id, operation).isPresent();
    }

    public boolean canUserDelete(final Long id) {
        return findById(id, PermissionOperation.DELETE).isPresent();
    }

    public boolean canUserDelete(final T entity) {
        return findById(getId(entity), PermissionOperation.DELETE).isPresent();
    }

    public boolean canUserUpdate(final Long id) {
        return findById(id, PermissionOperation.UPDATE).isPresent();
    }

    public boolean canUserUpdate(final T entity) {
        return findById(getId(entity), PermissionOperation.UPDATE).isPresent();
    }

    public boolean canUserRead(final Long id) {
        return findById(id, PermissionOperation.READ).isPresent();
    }

    public boolean canUserRead(final T entity) {
        return findById(getId(entity), PermissionOperation.READ).isPresent();
    }

    // FIND
    public List<T> forceFindAll() {
        final List<T> result;
        final Integer limit = DataHolder.getLimit();
        final Integer offset = DataHolder.getOffset();
        DataHolder.setLimit(Integer.MAX_VALUE);
        DataHolder.setOffset(0);
        result = findAll(BaseRepositoryImpl.DEFAULT_ORDER_BY);
        DataHolder.setLimit(limit);
        DataHolder.setOffset(offset);
        return result;
    }

    public List<T> findAll() {
        return findAll(isPrimary() && !Strings.isBlank(DataHolder.getSorting()) ? DataHolder.getSorting() : BaseRepositoryImpl.DEFAULT_ORDER_BY);
    }

    public List<T> findAll(final PermissionOperation operation) {
        return findAll(BaseRepositoryImpl.DEFAULT_ORDER_BY, operation);
    }

    //  sorting example: "modifiedDate:desc"
    public List<T> findAll(final String sorting) {
        final QueryConfig<T> queryConfig = baseQueryConfigBuilder()
                .sorting(actualSorting(sorting))
                .applySorting(true)
                .build();

        final FindQuery findQuery = queryBuilder.buildQuery(queryConfig);

        return getResult(findQuery);
    }

    public List<T> findAll(final String sorting, final PermissionOperation operation) {
        final QueryConfig<T> queryConfig = baseQueryConfigBuilder()
                .operation(operation)
                .sorting(sorting)
                .applySorting(true)
                .build();

        final FindQuery findQuery = queryBuilder.buildQuery(queryConfig);

        return getResult(findQuery);
    }

    public List<T> findAllBy(final Object... params) {
        final Integer limit = DataHolder.getLimit();
        final Integer offset = DataHolder.getOffset();
        DataHolder.setLimit(Integer.MAX_VALUE);
        DataHolder.setOffset(0);
        final List<T> result = findBy(params);
        DataHolder.setLimit(limit);
        DataHolder.setOffset(offset);
        return result;
    }

    public List<T> findByFields(final Object... params) {
        return findBy(params);
    }

    /*
     * if multiple filters are given, they are concatenated with AND
     * this behaves equally to giving one filter with all the sub-filters already concatenated
     * using this method the DataHolder-filter is ignored, it may be added as one filter parameter though
     * null filter parameters are ignored
     */
    public List<T> findWithFilter(
            final String... filters
    ) {
        return findWithSortingAndFilter(BaseRepositoryImpl.DEFAULT_ORDER_BY, filters);
    }

    public List<T> findWithSortingAndFilter(
            final String sorting,
            final String... filters
    ) {
        final List<String> actualFilters = Arrays.stream(filters)
                .filter(StringUtils::isNotBlank)
                .toList();
        final QueryConfig<T> queryConfig = baseQueryConfigBuilder()
                .sorting(actualSorting(sorting))
                .applySorting(true)
                .filter("(" + String.join(") AND (", actualFilters) + ")")
                .applyFilter(true)
                .build();

        final FindQuery findQuery = queryBuilder.buildQuery(queryConfig);

        return getResult(findQuery);
    }

    public Optional<T> findOne() {
        final Integer limit = DataHolder.getLimit();
        final Integer offset = DataHolder.getOffset();
        DataHolder.setLimit(1);
        DataHolder.setOffset(0);
        final List<T> result = findAll();
        DataHolder.setLimit(limit);
        DataHolder.setOffset(offset);
        return result.stream().findFirst();
    }

    public Optional<T> findOneBy(final Object... params) {
        final Integer limit = DataHolder.getLimit();
        final Integer offset = DataHolder.getOffset();
        DataHolder.setLimit(1);
        DataHolder.setOffset(0);
        final List<T> result = findBy(params);
        DataHolder.setLimit(limit);
        DataHolder.setOffset(offset);
        return result.stream().findFirst();
    }

    public Optional<T> findOneByFields(final Object... params) {
        final Integer limit = DataHolder.getLimit();
        final Integer offset = DataHolder.getOffset();
        DataHolder.setLimit(1);
        DataHolder.setOffset(0);
        final List<T> result = findByFields(params);
        DataHolder.setLimit(limit);
        DataHolder.setOffset(offset);
        return result.stream().findFirst();
    }

    public List<T> findBy(final Object... params) {
        if (params.length % 2 != 0) {
            throw new EGovException(ExceptionCode.FIND_BY_PARAMS_ERROR, "params requires at a field name and a value");
        }

        return orderByFindBy(
                PermissionOperation.READ,
                BaseRepositoryImpl.DEFAULT_ORDER_BY,
                params
        );
    }

    public List<T> findByAndOperation(final PermissionOperation operation, final Object... params) {
        return orderByFindBy(
                operation,
                BaseRepositoryImpl.DEFAULT_ORDER_BY,
                params
        );
    }

    public List<T> orderByFindBy(final String defaultSorting, final Object... params) {
        final QueryConfig<T> queryConfig = baseQueryConfigBuilder()
                .sorting(actualSorting(defaultSorting))
                .inputParamsWithColumnValuePairs(params)
                .build();

        final FindQuery findQuery = queryBuilder.buildQuery(queryConfig);

        return getResult(findQuery);
    }

    public List<T> orderByFindBy(final PermissionOperation operation, final String defaultSorting, final Object... params) {
        final QueryConfig<T> queryConfig = baseQueryConfigBuilder()
                .operation(operation)
                .sorting(actualSorting(defaultSorting))
                .inputParamsWithColumnValuePairs(params)
                .build();

        final FindQuery findQuery = queryBuilder.buildQuery(queryConfig);

        return getResult(findQuery);
    }

    protected Optional<T> findById(
            final Long id,
            final PermissionOperation operation,
            final boolean includingArchived
    ) {
        final QueryConfig<T> queryConfig = baseQueryConfigBuilder()
                .operation(operation)
                .includeArchived(includingArchived)
                .inputParamsWithColumnValuePairs(getIdFieldName(), id)
                .includeCountQuery(false)
                .build();

        final FindQuery findQuery = queryBuilder.buildQuery(queryConfig);

        return getResult(findQuery).stream().findFirst();
    }

    public Optional<T> findById(final Long id, final PermissionOperation operation) {
        return findById(id, operation, false);
    }

    public Optional<T> findById(final Long id) {
        return findById(id, PermissionOperation.READ);
    }

    @Deprecated(forRemoval = true)
    // todo: back compatability for curiaplus
    public Optional<T> findById(final Integer id) {
        return findById(id.longValue(), PermissionOperation.READ);
    }

    /**
     * @param ids size must be <= {@value QueryBuilder#IN_CONDITION_LIMIT}
     */
    public List<T> findAllById(final Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            // attention: this hides the possibly correct NoPermissionForEntityException
            return Collections.emptyList();
        }
        final QueryConfig<T> queryConfig = baseQueryConfigBuilder()
                .sorting(actualSorting(BaseRepositoryImpl.DEFAULT_ORDER_BY))
                .inputIds(ids)
                .build();

        final FindQuery findQuery = getQueryBuilder().buildQuery(queryConfig);

        return getResult(findQuery);
    }

    // CUSTOM SQL

    @Transactional
    public void withTransaction(final Runnable runnable) {
        runnable.run();
    }

    public List<T> runSql(final String query, final Object... values) {
        final FindQuery findQuery = getRunSqlFindQuery(query, values, actualSorting(BaseRepositoryImpl.DEFAULT_ORDER_BY));

        return getResult(findQuery);
    }

    public List<T> runSqlOptionRecompile(final String query, final Object... values) {
        final FindQuery findQuery = getRunSqlFindQuery(query, values, actualSorting(BaseRepositoryImpl.DEFAULT_ORDER_BY));
        findQuery.setQuery(findQuery.getQuery() + " OPTION (RECOMPILE)");

        return getResult(findQuery);
    }

    public List<T> orderByRunSql(final String sorting, final String query, final Object... values) {
        final FindQuery findQuery = getRunSqlFindQuery(query, values, actualSorting(sorting));

        return getResult(findQuery);
    }

    private FindQuery getRunSqlFindQuery(final String query, final Object[] values, final String sorting) {
        final QueryConfig.Builder<T> queryConfigBuilder = baseQueryConfigBuilder();

        if (query != null && !query.isEmpty()) {
            queryConfigBuilder.inputQuery(new ParametrizedQuery(query, values != null ? Arrays.asList(values) : Collections.emptyList()));
        }
        queryConfigBuilder.sorting(sorting);

        final QueryConfig<T> queryConfig = queryConfigBuilder.build();

        return queryBuilder.buildQuery(queryConfig);
    }

    // UTIL
    String actualSorting(final String defaultSorting) {
        return isPrimary() && !StringUtils.isBlank(DataHolder.getSorting())
                ? DataHolder.getSorting()
                : defaultSorting;
    }

    private List<T> getResult(final FindQuery findQuery) {
        final boolean includeCount = findQuery.isIncludeCount();
        try {
            final Filter filter = getEntityManager().unwrap(Session.class).enableFilter(ArchivedModifiableEntity.ARCHIVED_FILTER);
            filter.setParameter(ArchivedModifiableEntity.ARCHIVED_STATUS, false);
        } catch (final HibernateException e) {
            BaseRepositoryImpl.log.debug("archivedFilter not configured");
        }

        final Query resultQuery = getEntityManager().createNativeQuery(findQuery.getQuery());
        resultQuery.unwrap(NativeQuery.class).addEntity(getGenericParameterType());

        for (int i = 0; i < findQuery.getParameters().size(); i++) {
            resultQuery.setParameter(i + 1, findQuery.getParameters().get(i));
        }

        if (includeCount) {
            resultQuery.unwrap(NativeQuery.class).addScalar("NumberOfRecords", Integer.TYPE);
        }

        final List<?> results = resultQuery.getResultList();
        if (includeCount) {
            DataHolder.setCount(results.isEmpty()
                    ? 0
                    : (Integer) ((Object[]) results.get(0))[1]
            );
        }
        if (includeCount) {
            return results.stream()
                    .map(resultArray -> (T) ((Object[]) resultArray)[0])
                    .collect(toList());
        } else {
            return (List<T>) results;
        }
    }

    private boolean userCanExecuteOperation(final Long entityId) {
        final QueryConfig<T> queryConfig = baseQueryConfigBuilder()
                .operation(PermissionOperation.UPDATE)
                .inputParamsWithColumnValuePairs(
                        getIdFieldName(),
                        entityId
                )
                .build();

        final FindQuery findQuery = queryBuilder.buildQuery(queryConfig);

        final Query resultQuery = getEntityManager().createNativeQuery(findQuery.getRawQuery());
        resultQuery.setFlushMode(FlushModeType.COMMIT);
        for (int i = 0; i < findQuery.getParameters().size(); i++) {
            resultQuery.setParameter(i + 1, findQuery.getParameters().get(i));
        }
        return resultQuery.getResultList().stream().findFirst().isPresent();
    }

    private Class<T> getGenericParameterType() {
        final Class<? extends BaseRepositoryImpl> clazz = this.getClass();
        return clazz.getAnnotation(ClassType.class).entityClass();
    }

    public void setSkipCountQuery(final boolean skipCountQuery) {
        DataHolder.getRepositoryInstanceContext(this.toString()).setSkipCountQuery(skipCountQuery);
    }

    public IdentifiedResponse getIdentifiedResponse() {
        return new IdentifiedResponse(DataHolder.getRepositoryInstanceContext(this.toString()).getEntityIds());
    }

    public IdentifiedResponse getIdentifiedResponse(final String propertyName) {
        return new IdentifiedResponse(DataHolder.getRepositoryInstanceContext(this.toString()).getEntityIds(), propertyName);
    }

    public void deactivatePermissions() {
        DataHolder.getRepositoryInstanceContext(this.toString()).setSkipPermissions(true);
    }

    public void activatePermissions() {
        DataHolder.getRepositoryInstanceContext(this.toString()).setSkipPermissions(false);
    }

    public void deactivatePermissionsGlobally() {
        globalRepositoryConfigurationService.deactivateRepositoryPermissions(this.getClass());
    }

    public void activatePermissionsGlobally() {
        globalRepositoryConfigurationService.activateRepositoryPermissions(this.getClass());
    }

    private Long getId(final T entity) {
        final Object id = getEntityManager().getEntityManagerFactory()
                .getPersistenceUnitUtil()
                .getIdentifier(entity);
        return id instanceof final Integer intId
                ? intId.longValue()
                : (Long) id;
    }

    private Optional<Field> getIdField(final Class<? extends T> entityClazz) {
        for (final Field field : ReflectionUtils.getAllEntityFields(entityClazz)) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

    public boolean isPermissionsDeactivated() {
        return DataHolder.getRepositoryInstanceContext(this.toString()).isSkipPermissions()
                || globalRepositoryConfigurationService.isDeactivated(this.getClass());
    }

    private QueryConfig.Builder<T> baseQueryConfigBuilder() {
        return QueryConfig
                .builder(getEntityMetadata())
                .operation(PermissionOperation.READ)
                .filter(DataHolder.getFilter())
                .sorting(BaseRepositoryImpl.DEFAULT_ORDER_BY)
                .skipPermissions(isPermissionsDeactivated())
                .applyFilter(isPrimary())
                .applySorting(isPrimary())
                .applyPagination(isPrimary())
                .includeCountQuery(isPrimary() && !DataHolder.getRepositoryInstanceContext(this.toString()).isSkipCountQuery())
                .includeArchived(false)
                .inputQuery(null)
                .inputIds(Collections.emptyList())
                .inputParams(Collections.emptyList());
    }

    private EntityMetadata<T> getEntityMetadata() {
        return new EntityMetadata<>(
                getGenericParameterType(),
                getIdFieldName(),
                getTableName()
        );
    }

    public String getIdFieldName() {
        return getIdField(getGenericParameterType())
                .map((idField) -> nameTranslationComponent.getTranslatedColumnName(idField))
                .orElse("Id");
    }

    protected String getTableName() {
        return nameTranslationComponent.getTranslatedName(getGenericParameterType().getAnnotation(Table.class).name());
    }

}