package ch.ti8m.egov.framework.persistence.query;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityMetadata<T extends ModifiableEntity> {
    private final Class<T> clazz;
    private final String idFieldName;
    private final String tableName;

    public String getTableIdFieldName() {
        return tableName + "." + idFieldName;
    }
}
