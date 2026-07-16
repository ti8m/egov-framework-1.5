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
public class TermStrich {

    private Factor factor;
    private Optional<TermStrich> term;

    public Triple<String, List<Object>, TableJoins> toSqlString(
            final Class<? extends ModifiableEntity> clazz,
            final List<Object> parameters,
            final TableJoins joins,
            final NameTranslationComponent nameTranslationComponent,
            final JoinConfiguration joinConfiguration) {
        final Triple<String, List<Object>, TableJoins> sqlFactor = factor.toSqlString(clazz, parameters, joins, nameTranslationComponent, joinConfiguration);
        final Triple<String, List<Object>, TableJoins> sqlTerm;
        if (term.isPresent()) {
            sqlTerm = term.get().toSqlString(clazz, sqlFactor.getMiddle(), sqlFactor.getRight(), nameTranslationComponent, joinConfiguration);
        } else {
            sqlTerm = Triple.of("", sqlFactor.getMiddle(), sqlFactor.getRight());
        }
        return Triple.of(
                " AND " + sqlFactor.getLeft() + sqlTerm.getLeft(),
                sqlTerm.getMiddle(),
                sqlTerm.getRight()
        );
    }

}
