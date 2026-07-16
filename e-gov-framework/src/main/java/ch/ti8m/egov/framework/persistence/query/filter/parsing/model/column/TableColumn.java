package ch.ti8m.egov.framework.persistence.query.filter.parsing.model.column;

public class TableColumn {

    private final String qualifiedName;

    public TableColumn(final String table, final String column) {
        qualifiedName = table + "." + column;
    }

    @Override
    public String toString() {
        return qualifiedName;
    }
}
