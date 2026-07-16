package ch.ti8m.egov.framework.persistence.query.filter.parsing.model;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.persistence.query.JoinConfiguration;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.join.TableJoins;
import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import lombok.Builder;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Optional;

@Builder
public class Expression {

    private Term term;
    private Optional<ExpressionStrich> expressionStrich;

    public Triple<String, List<Object>, TableJoins> toSqlString(
            final Class<? extends ModifiableEntity> clazz,
            final List<Object> parameters,
            final TableJoins joins,
            final NameTranslationComponent nameTranslationComponent,
            final JoinConfiguration joinConfiguration) {

        final Triple<String, List<Object>, TableJoins> sqlTerm = term.toSqlString(clazz, parameters, joins, nameTranslationComponent, joinConfiguration);
        final Triple<String, List<Object>, TableJoins> sqlExpression;
        if (expressionStrich.isPresent()) {
            sqlExpression = expressionStrich.get().toSqlString(clazz, sqlTerm.getMiddle(), sqlTerm.getRight(), nameTranslationComponent, joinConfiguration);
        } else {
            sqlExpression = Triple.of("", sqlTerm.getMiddle(), sqlTerm.getRight());
        }
        return Triple.of(
                "(" + sqlTerm.getLeft() + sqlExpression.getLeft() + ")",
                sqlExpression.getMiddle(),
                sqlExpression.getRight()
        );
    }

}
