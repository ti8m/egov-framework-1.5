package ch.ti8m.egov.framework.persistence.query.filter.parsing.model.column;

import ch.ti8m.egov.framework.persistence.base.ArchivedModifiableEntity;
import lombok.Getter;

@Getter
public class ArchivedColumn extends TableColumn {

    public ArchivedColumn(final String table) {
        super(table, ArchivedModifiableEntity.Fields.archived);
    }
}
