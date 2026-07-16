package ch.ti8m.egov.framework.persistence.query.filter.parsing.model;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.persistence.query.JoinConfiguration;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.join.TableJoin;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.join.TableJoins;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.util.JoinUtil;
import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import lombok.Builder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Builder
public class FilterAst {

    private Expression expression;

    public Pair<String, List<Object>> compileForClass(
            final Class<? extends ModifiableEntity> clazz,
            final NameTranslationComponent nameTranslationComponent,
            final JoinConfiguration joinConfiguration) {

        final Triple<String, List<Object>, TableJoins> sql = expression.toSqlString(
                clazz,
                new ArrayList<>(),
                new TableJoins(),
                nameTranslationComponent,
                joinConfiguration);

        final Collection<TableJoin> tableJoins = sql.getRight().values();
        final String joinStatements = JoinUtil.toJoinStatements(tableJoins);

        final String whereConditions = sql.getLeft();
        final String whereStatement = " WHERE " + whereConditions;

        final List<Object> parameters = sql.getMiddle();
        return Pair.of(
                joinStatements + whereStatement,
                parameters
        );
    }

}
