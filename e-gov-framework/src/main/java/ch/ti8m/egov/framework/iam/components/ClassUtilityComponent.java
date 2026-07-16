package ch.ti8m.egov.framework.iam.components;


import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import jakarta.persistence.Id;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClassUtilityComponent {

    private static final Map<Class<?>, ClassProperties> CLASS_PROPERTIES = new ConcurrentHashMap<>();

    private static NameTranslationComponent nameTranslationComponent;

    @Autowired
    public ClassUtilityComponent(final NameTranslationComponent nameTranslationComponent) {
        ClassUtilityComponent.nameTranslationComponent = nameTranslationComponent;
    }

    public static String getIdFieldName(final Class<?> clazz) {
        if (!CLASS_PROPERTIES.containsKey(clazz)) {
            processClass(clazz);
        }
        return CLASS_PROPERTIES.get(clazz).getIdFieldName();
    }

    private static void processClass(final Class<?> clazz) {
        for (final Field field : FieldUtils.getAllFields(clazz)) {
            if (field.isAnnotationPresent(Id.class)) {
                CLASS_PROPERTIES.put(
                        clazz,
                        ClassProperties.builder()
                                .idFieldName(nameTranslationComponent.getTranslatedColumnName(field))
                                .build()
                );
                break;
            }
        }
    }

}
