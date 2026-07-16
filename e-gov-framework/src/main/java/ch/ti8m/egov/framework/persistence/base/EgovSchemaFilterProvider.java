package ch.ti8m.egov.framework.persistence.base;

import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.internal.DefaultSchemaFilter;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.hibernate.tool.schema.spi.SchemaFilterProvider;

/**
 * This filter is used to exclude sequences from being validated by Hibernate. This allows the use of sequences
 * that are managed directly by the database instead of JPA. This class is registered in the config file
 * egov-default.yml
 */
public class EgovSchemaFilterProvider implements SchemaFilterProvider {

    @Override
    public SchemaFilter getCreateFilter() {
        return DefaultSchemaFilter.INSTANCE;
    }

    @Override
    public SchemaFilter getDropFilter() {
        return DefaultSchemaFilter.INSTANCE;
    }

    @Override
    public SchemaFilter getTruncatorFilter() {
        return DefaultSchemaFilter.INSTANCE;
    }

    @Override
    public SchemaFilter getMigrateFilter() {
        return DefaultSchemaFilter.INSTANCE;
    }

    @Override
    public SchemaFilter getValidateFilter() {
        return EgovSchemaFilter.INSTANCE;
    }

    public static class EgovSchemaFilter extends DefaultSchemaFilter {
        public static final EgovSchemaFilter INSTANCE = new EgovSchemaFilter();

        @Override
        public boolean includeNamespace(Namespace namespace) {
            return true;
        }

        @Override
        public boolean includeTable(Table table) {
            return true;
        }

        @Override
        public boolean includeSequence(Sequence sequence) {
            return !sequence.getName().getSequenceName().getText().startsWith("egov_");
        }
    }
}
