package ch.ti8m.egov.framework.persistence.query;

import ch.ti8m.egov.framework.deployconfig.DatabaseConfigurationService;
import ch.ti8m.egov.framework.deployconfig.NamingStyle;
import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.exceptionhandling.context.FullTextSearchConfig;
import ch.ti8m.egov.framework.iam.api.model.PermissionOperation;
import ch.ti8m.egov.framework.persistence.base.FindQuery;
import ch.ti8m.egov.framework.persistence.base.Sorting;
import ch.ti8m.egov.framework.persistence.query.archived.ArchivedFilterBuilder;
import ch.ti8m.egov.framework.persistence.query.filter.FilterBuilder;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.FilterParser;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.tokenizer.Tokenizer;
import ch.ti8m.egov.framework.persistence.query.permission.PermissionBuilder;
import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import ch.ti8m.egov.testbase.entities.TestArchivedModifiableEntity;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryBuilderTest {

    private final OrderAndPaginationBuilder orderAndPaginationBuilder = mock(OrderAndPaginationBuilder.class);
    private final CountQueryBuilder countQueryBuilder = mock(CountQueryBuilder.class);
    private final PermissionBuilder permissionBuilder = mock(PermissionBuilder.class);
    private final ArchivedFilterBuilder archivedFilterBuilder = mock(ArchivedFilterBuilder.class);
    private final FullTextSearchQueryBuilder fullTextSearchQueryBuilder = mock(FullTextSearchQueryBuilder.class);
    private final DatabaseConfigurationService databaseConfigurationService = mock(DatabaseConfigurationService.class);

    private QueryBuilder queryBuilder;

    private static EntityMetadata<TestArchivedModifiableEntity> entityMetadata() {
        return new EntityMetadata<>(TestArchivedModifiableEntity.class, "id", "CP_TEST");
    }

    @BeforeEach
    void init() {
        when(databaseConfigurationService.getNamingStyle())
                .thenReturn(NamingStyle.CAMEL_CASE);

        final NameTranslationComponent nameTranslationComponent = new NameTranslationComponent(databaseConfigurationService);
        final FilterBuilder filterBuilder = new FilterBuilder(new FilterParser(new Tokenizer()), nameTranslationComponent);

        DataHolder.setUserId(1L);
        DataHolder.setFilter("id == 1");
        final FullTextSearchConfig fullTextSearchConfig = DataHolder.getFullTextSearchConfig();
        fullTextSearchConfig
                .addField("testField", 1);

        when(permissionBuilder.getPermissions(DataHolder.getUserId(), TestArchivedModifiableEntity.class, PermissionOperation.READ))
                .thenReturn(new ParametrizedQuery("PERMISSION_FILTER_QUERY", List.of("PERMISSION_FILTER_PARAM")));

        when(archivedFilterBuilder.getArchivedFilter(anyString()))
                .thenReturn(new ParametrizedQuery("ARCHIVED_FILTER_QUERY", List.of("ARCHIVED_FILTER_PARAM")));

        when(fullTextSearchQueryBuilder.getWeightedSearchJoin(anyString(), anyString()))
                .thenReturn(Pair.of(" JOIN FULLTEXT_SEARCH", List.of("FULL_TEXT_SEARCH_PARAM")));

        when(orderAndPaginationBuilder.getOrderAndPaginationExtension(any(), anyString(), anyBoolean(), eq(true)))
                .thenReturn(Triple.of(" JOIN ORDER_BY_AND_PAGING_TABLES", " ORDER_AND_PAGING_CLAUSE", "ORDER_BY_AND_PAGING_COLUMNS"));

        when(orderAndPaginationBuilder.getOrderAndPaginationExtension(any(), anyString(), anyBoolean(), eq(false)))
                .thenReturn(Triple.of(" JOIN ORDER_BY_AND_PAGING_TABLES", " ORDER_CLAUSE", "ORDER_BY_AND_PAGING_COLUMNS"));

        when(countQueryBuilder.buildCountQuery(anyString()))
                .thenReturn("COUNT_QUERY");

        queryBuilder = new QueryBuilder(
                orderAndPaginationBuilder,
                filterBuilder,
                countQueryBuilder,
                permissionBuilder,
                archivedFilterBuilder,
                fullTextSearchQueryBuilder,
                databaseConfigurationService
        );
    }

    @Test
    void correctFilterForNumberInput() {
        final ParametrizedQuery expected = new ParametrizedQuery(" WHERE ((((((rootTable.id = ?))))))", List.of(12L));

        final ParametrizedQuery actual = queryBuilder.appendInputFilter("", Collections.emptySet(), List.of(Pair.of("id", 12)), QueryBuilderTest.entityMetadata(), true);

        assertThat(actual.getQuery()).isEqualTo(expected.getQuery());
        assertThat(actual.getParameters()).containsExactlyElementsOf(expected.getParameters());
    }

    @Test
    void correctFilterForStringInput() {
        final ParametrizedQuery expected = new ParametrizedQuery(" WHERE ((((((rootTable.testField = ?))))))", List.of("hallo"));
        final ParametrizedQuery actual = queryBuilder.appendInputFilter("", Collections.emptySet(), List.of(Pair.of("testField", "hallo")), QueryBuilderTest.entityMetadata(), true);

        assertThat(actual.getQuery()).isEqualTo(expected.getQuery());
        assertThat(actual.getParameters()).containsExactlyElementsOf(expected.getParameters());
    }

    @Test
    void correctFilterForBooleanInput() {
        final ParametrizedQuery expected = new ParametrizedQuery(" WHERE ((((((rootTable.isTestEntity = ?))))))", List.of(true));
        final ParametrizedQuery actual = queryBuilder.appendInputFilter("", Collections.emptySet(), List.of(Pair.of("isTestEntity", true)), QueryBuilderTest.entityMetadata(), true);

        assertThat(actual.getQuery()).isEqualTo(expected.getQuery());
        assertThat(actual.getParameters()).containsExactlyElementsOf(expected.getParameters());
    }

    @Test
    void correctFilterForZonedDateTimeInput() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(
                2024,
                12,
                31,
                15,
                30,
                0,
                0,
                ZoneId.of("CET")
        );
        final var expected = new ParametrizedQuery(" WHERE ((((((rootTable.modifiedDate = ?))))))", List.of(zonedDateTime));

        final var actual = queryBuilder.appendInputFilter("", Collections.emptySet(), List.of(Pair.of("modifiedDate", zonedDateTime)), QueryBuilderTest.entityMetadata(), true);

        assertThat(actual.getQuery()).isEqualTo(expected.getQuery());
        assertThat(actual.getParameters()).containsExactlyElementsOf(expected.getParameters());
    }

    @Test
    void emptyParamsFilledIds() {
        final var expected = new ParametrizedQuery(" WHERE (((rootTable.id IN (1, 2, 3))))", Collections.emptyList());

        final var actual = queryBuilder.appendInputFilter("", List.of(1L, 2L, 3L), List.of(), QueryBuilderTest.entityMetadata(), true);

        assertThat(actual.getQuery()).isEqualTo(expected.getQuery());
        assertThat(actual.getParameters()).containsExactlyElementsOf(expected.getParameters());
    }

    @Test
    void emptyParamsEmptyIds() {
        final var expected = new ParametrizedQuery("", Collections.emptyList());

        final var actual = queryBuilder.appendInputFilter("", Collections.emptyList(), List.of(), QueryBuilderTest.entityMetadata(), true);

        assertThat(actual.getQuery()).isEqualTo(expected.getQuery());
        assertThat(actual.getParameters()).containsExactlyElementsOf(expected.getParameters());
    }

    @Test
    void build_WithIdsAndColumConditionsAndAllFilters() {
        final QueryConfig<TestArchivedModifiableEntity> queryConfig = QueryConfig.builder(QueryBuilderTest.entityMetadata())
                .operation(PermissionOperation.READ)
                .filter(DataHolder.getFilter())
                .applyFilter(true)
                .sorting(Sorting.builder().field("id").ascending().get())
                .applySorting(true)
                .skipPermissions(false)
                .includeCountQuery(true)
                .includeArchived(false)
                .applyPagination(false)
                .inputIds(List.of(1L, 2L, 3L))
                .inputParamsWithColumnValuePairs("testField1", "TestValue1", "testField2", "TestValue2")
                .build();

        final FindQuery findQuery = queryBuilder.buildQuery(queryConfig);

        assertThat(findQuery.getQuery())
                .isEqualTo(
                        "SELECT CP_TEST.*" +
                                ", COUNT(CP_TEST.id) OVER() NumberOfRecords," +
                                " ORDER_BY_AND_PAGING_COLUMNS" +
                                " FROM CP_TEST" +
                                " JOIN ORDER_BY_AND_PAGING_TABLES" +
                                " JOIN FULLTEXT_SEARCH"
                                + " WHERE CP_TEST.id IN (SELECT DISTINCT rootTable.id FROM CP_TEST rootTable WHERE ((((((rootTable.testField1 = ?)))) AND ((((rootTable.testField2 = ?)))) AND (rootTable.id IN (1, 2, 3)))))"
                                + " AND CP_TEST.id IN (SELECT DISTINCT rootTable.id FROM CP_TEST rootTable WHERE (((rootTable.id = ?))))"
                                + " AND CP_TEST.id IN (PERMISSION_FILTER_QUERY)"
                                + " AND CP_TEST.id NOT IN (ARCHIVED_FILTER_QUERY)"
                                + " ORDER_CLAUSE"
                );

        assertThat(findQuery.getCountQuery()).isEqualTo("COUNT_QUERY");
        assertThat(findQuery.getRawQuery()).isEqualTo(
                "SELECT idSelectTable.id" +
                        " FROM (SELECT CP_TEST.*, COUNT(CP_TEST.id) OVER() NumberOfRecords," +
                        " ORDER_BY_AND_PAGING_COLUMNS" +
                        " FROM CP_TEST" +
                        " JOIN ORDER_BY_AND_PAGING_TABLES" +
                        " JOIN FULLTEXT_SEARCH"
                        + " WHERE CP_TEST.id IN (SELECT DISTINCT rootTable.id FROM CP_TEST rootTable WHERE ((((((rootTable.testField1 = ?)))) AND ((((rootTable.testField2 = ?)))) AND (rootTable.id IN (1, 2, 3)))))"
                        + " AND CP_TEST.id IN (SELECT DISTINCT rootTable.id FROM CP_TEST rootTable WHERE (((rootTable.id = ?))))"
                        + " AND CP_TEST.id IN (PERMISSION_FILTER_QUERY)"
                        + " AND CP_TEST.id NOT IN (ARCHIVED_FILTER_QUERY)"
                        + ") idSelectTable"
        );

        final List<Object> findQueryParameters = findQuery.getParameters();

        assertThat(findQueryParameters).containsExactly("FULL_TEXT_SEARCH_PARAM", "TestValue1", "TestValue2", 1L, "PERMISSION_FILTER_PARAM", "ARCHIVED_FILTER_PARAM");

    }

    @Test
    void whenPaginationIsRequested_thenPaginationClauseIsAdded() {
        final QueryConfig<TestArchivedModifiableEntity> queryConfig = QueryConfig.builder(QueryBuilderTest.entityMetadata())
                .operation(PermissionOperation.READ)
                .filter(DataHolder.getFilter())
                .applyFilter(true)
                .sorting(Sorting.builder().field("id").ascending().get())
                .applySorting(true)
                .skipPermissions(false)
                .includeCountQuery(true)
                .includeArchived(false)
                .applyPagination(true)
                .inputIds(List.of(1L, 2L, 3L))
                .inputParamsWithColumnValuePairs("testField1", "TestValue1", "testField2", "TestValue2")
                .build();

        final FindQuery findQuery = queryBuilder.buildQuery(queryConfig);

        assertThat(findQuery.getQuery())
                .isEqualTo(
                        "SELECT CP_TEST.*" +
                                ", COUNT(CP_TEST.id) OVER() NumberOfRecords," +
                                " ORDER_BY_AND_PAGING_COLUMNS" +
                                " FROM CP_TEST" +
                                " JOIN ORDER_BY_AND_PAGING_TABLES" +
                                " JOIN FULLTEXT_SEARCH"
                                + " WHERE CP_TEST.id IN (SELECT DISTINCT rootTable.id FROM CP_TEST rootTable WHERE ((((((rootTable.testField1 = ?)))) AND ((((rootTable.testField2 = ?)))) AND (rootTable.id IN (1, 2, 3)))))"
                                + " AND CP_TEST.id IN (SELECT DISTINCT rootTable.id FROM CP_TEST rootTable WHERE (((rootTable.id = ?))))"
                                + " AND CP_TEST.id IN (PERMISSION_FILTER_QUERY)"
                                + " AND CP_TEST.id NOT IN (ARCHIVED_FILTER_QUERY)"
                                + " ORDER_AND_PAGING_CLAUSE"
                );

        assertThat(findQuery.getCountQuery()).isEqualTo("COUNT_QUERY");
        assertThat(findQuery.getRawQuery()).isEqualTo(
                "SELECT idSelectTable.id" +
                        " FROM (SELECT CP_TEST.*, COUNT(CP_TEST.id) OVER() NumberOfRecords," +
                        " ORDER_BY_AND_PAGING_COLUMNS" +
                        " FROM CP_TEST" +
                        " JOIN ORDER_BY_AND_PAGING_TABLES" +
                        " JOIN FULLTEXT_SEARCH"
                        + " WHERE CP_TEST.id IN (SELECT DISTINCT rootTable.id FROM CP_TEST rootTable WHERE ((((((rootTable.testField1 = ?)))) AND ((((rootTable.testField2 = ?)))) AND (rootTable.id IN (1, 2, 3)))))"
                        + " AND CP_TEST.id IN (SELECT DISTINCT rootTable.id FROM CP_TEST rootTable WHERE (((rootTable.id = ?))))"
                        + " AND CP_TEST.id IN (PERMISSION_FILTER_QUERY)"
                        + " AND CP_TEST.id NOT IN (ARCHIVED_FILTER_QUERY)"
                        + ") idSelectTable"
        );

        final List<Object> findQueryParameters = findQuery.getParameters();

        assertThat(findQueryParameters).containsExactly("FULL_TEXT_SEARCH_PARAM", "TestValue1", "TestValue2", 1L, "PERMISSION_FILTER_PARAM", "ARCHIVED_FILTER_PARAM");

    }
}
