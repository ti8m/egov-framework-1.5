package ch.ti8m.egov.framework.persistence.query.filter.parsing.model.join;

public abstract class TableJoin {

    protected abstract String getJoinClause();

    @Override
    public String toString() {
        return getJoinClause();
    }
}
