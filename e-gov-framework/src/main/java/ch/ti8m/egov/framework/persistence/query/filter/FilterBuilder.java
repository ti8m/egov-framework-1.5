package ch.ti8m.egov.framework.persistence.query.filter;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.persistence.query.JoinConfiguration;
import ch.ti8m.egov.framework.persistence.query.ParametrizedQuery;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.FilterParser;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.FilterAst;
import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FilterBuilder {

    private final FilterParser filterParser;
    private final NameTranslationComponent nameTranslationComponent;

    @Autowired
    public FilterBuilder(
            final FilterParser filterParser,
            final NameTranslationComponent nameTranslationComponent
    ) {
        this.filterParser = filterParser;
        this.nameTranslationComponent = nameTranslationComponent;
    }

    /**
     * <ul>
     *     <li>Creates WHERE part</li>
     *     <li>Adds JOIN-Clauses if {@code filter} contains filter conditions on sub entities</li>
     *     <li>Replaces values with ? placeholder</li>
     *     <li>Adds values for IN conditions directly if they are numeric (e.g. adds {@code rootTable.id IN (1, 2, 3)})</li>
     * </ul>
     * <p>
     * <b>WARNING!</b> Do not use with IN Conditions with more than {@value ch.ti8m.egov.framework.persistence.query.QueryBuilder#IN_CONDITION_LIMIT} values. Can lead to {@link StackOverflowError}
     *
     * @param selectFromJoinQuery SQL containing {@code SELECT}..{@code FROM}..({@code JOIN..})
     * @param filter              filter conditions. If empty, this method returns {@code selectFromJoinQuery}
     * @param clazz               the entity class to apply this filter on
     * @param joinConfiguration   configuration for joining tables
     * @return Key: JOINs + WHERE part derived from {@code filter}. Value: list of extracted parameters from {@code filter}
     */
    public <T extends ModifiableEntity> ParametrizedQuery getFilterQuery(
            final String selectFromJoinQuery,
            final String filter,
            final Class<T> clazz,
            final JoinConfiguration joinConfiguration) {
        if (filter.isEmpty()) {
            return new ParametrizedQuery(selectFromJoinQuery, Collections.emptyList());
        }
        final FilterAst filterAst = filterParser.parse(filter);
        final Pair<String, List<Object>> sql = filterAst.compileForClass(clazz, nameTranslationComponent, joinConfiguration);

        final String joinsAndWhereStatement = sql.getLeft();

        final List<Object> parameters = sql.getRight().stream()
                .map(o -> o instanceof String string
                        ? string.replace("\\'", "'")
                        : o)
                .toList();

        return new ParametrizedQuery(
                selectFromJoinQuery + joinsAndWhereStatement,
                parameters
        );
    }

}
