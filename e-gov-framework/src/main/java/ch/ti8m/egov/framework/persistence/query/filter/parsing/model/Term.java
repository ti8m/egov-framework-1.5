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
public class Term {

    private Factor factor;
    private Optional<TermStrich> termStrich;

    public Triple<String, List<Object>, TableJoins> toSqlString(
            final Class<? extends ModifiableEntity> clazz,
            final List<Object> parameters,
            final TableJoins joins,
            final NameTranslationComponent nameTranslationComponent,
            final JoinConfiguration joinConfiguration) {

        final Triple<String, List<Object>, TableJoins> sqlFactor = factor.toSqlString(clazz, parameters, joins, nameTranslationComponent, joinConfiguration);
        final Triple<String, List<Object>, TableJoins> sqlTermStrich;
        if (termStrich.isPresent()) {
            sqlTermStrich = termStrich.get().toSqlString(clazz, sqlFactor.getMiddle(), sqlFactor.getRight(), nameTranslationComponent, joinConfiguration);
        } else {
            sqlTermStrich = Triple.of("", sqlFactor.getMiddle(), sqlFactor.getRight());
        }
        return Triple.of(
                "(" + sqlFactor.getLeft() + sqlTermStrich.getLeft() + ")",
                sqlTermStrich.getMiddle(),
                sqlTermStrich.getRight()
        );
    }

}
