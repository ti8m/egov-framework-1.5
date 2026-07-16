package ch.ti8m.egov.framework.persistence.query;

import ch.ti8m.egov.framework.deployconfig.DatabaseConfigurationService;
import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.iam.api.model.PermissionOperation;
import ch.ti8m.egov.framework.persistence.base.ArchivedModifiableEntity;
import ch.ti8m.egov.framework.persistence.base.FindQuery;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.persistence.query.archived.ArchivedFilterBuilder;
import ch.ti8m.egov.framework.persistence.query.filter.FilterBuilder;
import ch.ti8m.egov.framework.persistence.query.permission.PermissionBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryBuilder {

    public static final String ROOT_TABLE_ALIAS = "rootTable";
    public static final int IN_CONDITION_LIMIT = 999;
    private final OrderAndPaginationBuilder orderAndPaginationBuilder;
    private final FilterBuilder filterBuilder;
    private final CountQueryBuilder countQueryBuilder;
    private final PermissionBuilder permissionBuilder;
    private final ArchivedFilterBuilder archivedFilterBuilder;
    private final FullTextSearchQueryBuilder fullTextSearchQueryBuilder;
    private final DatabaseConfigurationService databaseConfigurationService;
    @Value("${egov.persistence.database.filter.name-may-be-column-name:false}")
    private boolean fieldNameToBeDatabaseColumnNameAllowed;

    /**
     * @param inputIds size must be <= {@value IN_CONDITION_LIMIT}
     */
    protected <T extends ModifiableEntity> ParametrizedQuery appendInputFilter(
            final String baseQuery,
            final Collection<Long> inputIds,
            final List<Pair<String, Object>> inputParams,
            final EntityMetadata<T> entityMetadata,
            final Boolean includeArchived) {
        if (inputParams.isEmpty() && inputIds.isEmpty()) {
            return new ParametrizedQuery(baseQuery, Collections.emptyList());
        }
        if (inputIds.size() > QueryBuilder.IN_CONDITION_LIMIT) {
            throw new EGovException(ExceptionCode.ID_LIMIT_EXCEEDED, "Size of given ids must be <= " + QueryBuilder.IN_CONDITION_LIMIT + ", but was " + inputIds.size());
        }
        final StringJoiner filterConditionAndJoiner = new StringJoiner(" AND ");

        inputParams.forEach(columnAndValue -> {
            final String column = columnAndValue.getLeft();
            final Object value = columnAndValue.getRight();

            if (value == null) {
                filterConditionAndJoiner.add("(" + column + " IS NULL)");
            } else if (value instanceof Number || value instanceof Boolean || value instanceof Temporal) {
                filterConditionAndJoiner.add("(" + column + " == " + value + ")");
            } else {
                filterConditionAndJoiner.add("(" + column + " == '" + value + "')");
            }
        });
        if (!inputIds.isEmpty()) {
            final String idsCommaSeparated = inputIds.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            final String inClause = String.format("%s IN (%s)", entityMetadata.getIdFieldName(), idsCommaSeparated);
            filterConditionAndJoiner.add(inClause);
        }
        final String filter = filterConditionAndJoiner.toString();

        final JoinConfiguration joinConfiguration = new JoinConfiguration(
                fieldNameToBeDatabaseColumnNameAllowed,
                true,
                includeArchived,
                databaseConfigurationService.getFalseStatement()
        );

        return filterBuilder.getFilterQuery(baseQuery, filter, entityMetadata.getClazz(), joinConfiguration);
    }

    public <T extends ModifiableEntity> FindQuery buildQuery(final QueryConfig<T> config) {
        final EntityMetadata<T> entityMetadata = config.getEntityMetadata();
        final String baseQuery = getBaseQuery(entityMetadata.getTableName(), entityMetadata.getIdFieldName());
        ParametrizedQuery inputQuery = config.getInputQuery();
        if (inputQuery == null) {
            inputQuery = appendInputFilter(baseQuery, config.getInputIds(), config.getInputParams(), entityMetadata, config.isIncludeArchived());
        }

        final Triple<String, String, String> orderAndPaginationExtension = config.isApplySorting()
                ? orderAndPaginationBuilder.getOrderAndPaginationExtension(entityMetadata.getClazz(), config.getSorting(), config.isIncludeArchived(), config.isApplyPagination())
                : Triple.of("", "", "");

        ParametrizedQuery parametrizedQuery = getParametrizedQuery(inputQuery, entityMetadata.getTableName(), entityMetadata.getIdFieldName(), config.isIncludeCountQuery(), orderAndPaginationExtension);

        if (config.isApplyFilter() && isNotEmpty(config.getFilter())) {
            parametrizedQuery = appendQueryFilter(parametrizedQuery, config.getFilter(), entityMetadata, config.isIncludeArchived());
        }

        if (!config.isSkipPermissions()) {
            parametrizedQuery = appendPermissionsFilter(parametrizedQuery, config.getOperation(), entityMetadata);
        }

        if (!config.isIncludeArchived() && ArchivedModifiableEntity.class.isAssignableFrom(entityMetadata.getClazz())) {
            parametrizedQuery = appendArchivedFilter(parametrizedQuery, baseQuery, entityMetadata.getTableName(), entityMetadata.getIdFieldName());
        }

        DataHolder.getFullTextSearchConfig().clear();

        return FindQuery.builder()
                .query(parametrizedQuery.getQuery() + orderAndPaginationExtension.getMiddle())
                .parameters(parametrizedQuery.getParameters())
                .countQuery(countQueryBuilder.buildCountQuery(parametrizedQuery.getQuery()))
                .includeCount(config.isIncludeCountQuery())
                .rawQuery("SELECT idSelectTable." + entityMetadata.getIdFieldName() + " FROM (" + parametrizedQuery.getQuery() + ") idSelectTable")
                .build();
    }

    protected ParametrizedQuery getParametrizedQuery(
            final ParametrizedQuery inputQuery,
            final String tableName,
            final String idFieldName,
            final boolean includeCount,
            final Triple<String, String, String> orderAndPaginationExtension
    ) {
        final StringBuilder queryBuilder = new StringBuilder("SELECT " + tableName + ".*"
                + (includeCount ? ", COUNT(" + tableName + "." + idFieldName + ") OVER() NumberOfRecords" : "")
                + (StringUtils.isBlank(orderAndPaginationExtension.getRight()) ? "" : ", " + orderAndPaginationExtension.getRight())
                + " FROM " + tableName
                + orderAndPaginationExtension.getLeft());

        final List<Object> parameters = new ArrayList<>();
        if (DataHolder.getFullTextSearchConfig().isFullTextSearchActive()) {
            final Pair<String, List<Object>> weightedSearchJoin = fullTextSearchQueryBuilder.getWeightedSearchJoin(tableName, idFieldName);
            queryBuilder.append(weightedSearchJoin.getLeft());
            parameters.addAll(weightedSearchJoin.getRight());
        }

        queryBuilder.append(String.format(" WHERE %s.%s IN (%s)", tableName, idFieldName, inputQuery.getQuery()));
        parameters.addAll(inputQuery.getParameters());

        return new ParametrizedQuery(queryBuilder.toString(), parameters);
    }

    protected ParametrizedQuery appendArchivedFilter(
            final ParametrizedQuery parametrizedQuery,
            final String baseQuery,
            final String tableName,
            final String idFieldName
    ) {
        final ParametrizedQuery archivedFilter = archivedFilterBuilder.getArchivedFilter(baseQuery);
        return appendGenericExclusionFilter(parametrizedQuery, archivedFilter, tableName + "." + idFieldName);
    }

    protected <T extends ModifiableEntity> ParametrizedQuery appendPermissionsFilter(
            final ParametrizedQuery parametrizedQuery,
            final PermissionOperation operation,
            final EntityMetadata<T> entityMetadata
    ) {
        final ParametrizedQuery permissionsFilterQuery = permissionBuilder.getPermissions(
                DataHolder.getUserId(), entityMetadata.getClazz(), operation
        );
        return appendGenericFilter(parametrizedQuery, permissionsFilterQuery, entityMetadata.getTableIdFieldName());
    }

    protected ParametrizedQuery appendPermissionsFilter(
            final ParametrizedQuery parametrizedQuery,
            final String tableName,
            final String idFieldName,
            final PermissionOperation operation
    ) {
        final ParametrizedQuery permissionsFilterQuery = permissionBuilder.getPermissions(
                DataHolder.getUserId(), tableName, idFieldName, operation
        );
        return appendGenericFilter(parametrizedQuery, permissionsFilterQuery, tableName + "." + idFieldName);
    }

    protected <T extends ModifiableEntity> ParametrizedQuery appendQueryFilter(
            final ParametrizedQuery parametrizedQuery,
            final String filter,
            final EntityMetadata<T> entityMetadata,
            final boolean includeArchived) {
        final String baseQuery = getBaseQuery(entityMetadata.getTableName(), entityMetadata.getIdFieldName());

        final JoinConfiguration joinConfiguration = new JoinConfiguration(
                fieldNameToBeDatabaseColumnNameAllowed,
                true,
                includeArchived,
                databaseConfigurationService.getFalseStatement()
        );

        final ParametrizedQuery filterQuery = filterBuilder.getFilterQuery(
                baseQuery,
                filter,
                entityMetadata.getClazz(),
                joinConfiguration
        );

        return appendGenericFilter(parametrizedQuery, filterQuery, entityMetadata.getTableIdFieldName());
    }

    protected ParametrizedQuery appendGenericFilter(
            final ParametrizedQuery parametrizedQuery,
            final ParametrizedQuery filterQuery,
            final String tableIdFieldName
    ) {
        final String query = parametrizedQuery.getQuery() + " AND " + tableIdFieldName
                + " IN (" + filterQuery.getQuery() + ")";

        final List<Object> params = new LinkedList<>(parametrizedQuery.getParameters());
        params.addAll(filterQuery.getParameters());

        return new ParametrizedQuery(query, params);
    }

    protected ParametrizedQuery appendGenericExclusionFilter(
            final ParametrizedQuery parametrizedQuery,
            final ParametrizedQuery filterQuery,
            final String tableIdFieldName
    ) {
        final String query = parametrizedQuery.getQuery()
                + " AND " + tableIdFieldName + " NOT IN (" + filterQuery.getQuery() + ")";

        final List<Object> params = new LinkedList<>(parametrizedQuery.getParameters());
        params.addAll(filterQuery.getParameters());

        return new ParametrizedQuery(query, params);
    }


    protected String getBaseQuery(final String tableName, final String idFieldName) {
        return "SELECT DISTINCT " + QueryBuilder.ROOT_TABLE_ALIAS + "." + idFieldName + " FROM " + tableName + " " + QueryBuilder.ROOT_TABLE_ALIAS;
    }
}
