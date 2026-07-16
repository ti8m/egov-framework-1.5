package ch.ti8m.egov.framework.persistence.query.filter.parsing.model.join;

import ch.ti8m.egov.framework.persistence.query.JoinConfiguration;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.column.ArchivedColumn;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.column.TableColumn;

public class LeftJoin extends TableJoin {

    private final String joinClause;

    public LeftJoin(final String joinTable,
                    final TableColumn tableJoinColumn,
                    final TableColumn joinTableJoinColumn,
                    final boolean isJoinTableArchivable,
                    final JoinConfiguration joinConfiguration) {
        String leftJoin = " LEFT JOIN " + joinTable + " ON " + joinTableJoinColumn + " = " + tableJoinColumn;

        if (isJoinTableArchivable && !joinConfiguration.isArchivedEntitiesIncluded()) {
            final ArchivedColumn joinTableArchivedColumn = new ArchivedColumn(joinTable);
            leftJoin += " AND " + joinTableArchivedColumn + " = " + joinConfiguration.getDbAwareFalseStatement();
        }
        this.joinClause = leftJoin;
    }

    @Override
    protected String getJoinClause() {
        return joinClause;
    }
}
