package ch.ti8m.egov.framework.persistence.util;

import ch.ti8m.egov.framework.deployconfig.DatabaseConfigurationService;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class NameTranslationComponent {

    private final DatabaseConfigurationService databaseConfigurationService;

    @Autowired
    public NameTranslationComponent(DatabaseConfigurationService databaseConfigurationService) {
        this.databaseConfigurationService = databaseConfigurationService;
    }

    public String getTranslatedName(final String name) {
        return switch (databaseConfigurationService.getNamingStyle()) {
            case CAMEL_CASE -> name;
            case SNAKE_CASE -> name.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        };
    }

    public String getTranslatedColumnName(final Field field) {
        if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isBlank()) {
            return field.getAnnotation(Column.class).name();
        }
        return getTranslatedName(field.getName());
    }

    public String getTranslatedEntityName(final Class<?> entity) {
        if (entity.isAnnotationPresent(Table.class)) {
            return entity.getAnnotation(Table.class).name();
        }
        return getTranslatedName(entity.getSimpleName());
    }

}
