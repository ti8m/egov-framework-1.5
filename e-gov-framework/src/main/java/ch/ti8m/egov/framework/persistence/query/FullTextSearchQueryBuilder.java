package ch.ti8m.egov.framework.persistence.query;

import ch.ti8m.egov.framework.deployconfig.DatabaseConfigurationService;
import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.exceptionhandling.context.FullTextSearchConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullTextSearchQueryBuilder {

    private final DatabaseConfigurationService databaseConfigurationService;

    private String getJoinForTimeRelevance(final String tableName) {
        if (DataHolder.getFullTextSearchConfig().isTimeRelevanceActive()) {
            return String.format("JOIN %s AS SearchTable ON MatchingElements.[KEY] = SearchTable.PK", tableName);
        }
        return "";
    }

    private String selectRankForSearchField(
            final Map.Entry<String, Integer> field,
            final String tableName,
            final List<Object> parameters
    ) {
        parameters.add(DataHolder.getSearch());
        return String.format(
                "SELECT Rank * %d AS Rank, [KEY] FROM FREETEXTTABLE(%s, %s, ?)",
                field.getValue(), tableName, field.getKey()
        );
    }

    protected Pair<String, List<Object>> getWeightedSearchJoin(final String tableName,
                                                               final String idFieldName) {
        final List<Object> parameters = new ArrayList<>();
        final FullTextSearchConfig fulltextSearchConfig = DataHolder.getFullTextSearchConfig();

        final String innerQuery = fulltextSearchConfig.getFields().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(field -> selectRankForSearchField(field, tableName, parameters))
                .collect(Collectors.joining(" UNION ALL "));

        final String joinQuery = String.format(" JOIN (SELECT [KEY], SUM(Rank * %s) AS WeightedRank FROM (%s)"
                        + " AS MatchingElements %s GROUP BY [KEY]) AS Ranks ON Ranks.[KEY] = %s.%s",
                fulltextSearchConfig.getTimeRelevanceMultiplier().replace(FullTextSearchConfig.CURRENT_DATE_PLACEHOLDER, databaseConfigurationService.getCurrentDateString()),
                innerQuery,
                getJoinForTimeRelevance(tableName),
                tableName,
                idFieldName);

        return Pair.of(joinQuery, parameters);
    }
}
