package ch.ti8m.egov.framework.persistence.util;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionUtilsTest {

    static final List<String> MODIFIABLE_ENTITY_FIELDS = List.of("createdBy", "modifiedBy", "createdDate", "modifiedDate");
    static final List<String> MODIFIABLE_ENTITY_STATIC_FIELDS = List.of("CODE_LENGTH");

    static String[] getChildClassFields(final boolean includeStatic) {
        final List<String> expectedFieldNames = new ArrayList<>(MODIFIABLE_ENTITY_FIELDS);

        if (includeStatic) {
            expectedFieldNames.addAll(MODIFIABLE_ENTITY_STATIC_FIELDS);
            expectedFieldNames.add("staticField");
        }
        expectedFieldNames.addAll(List.of("parentField", "childField"));

        return expectedFieldNames.toArray(String[]::new);

    }

    @ParameterizedTest(name = "Get all entity fields: {0}")
    @CsvSource({"Only instance fields, false", "Including static fields, true"})
    void getAllEntityFields(final String readableName, final boolean includeStatic) {
        final String[] expectedFieldNames = getChildClassFields(includeStatic);

        final List<Field> actualFields = ReflectionUtils.getAllEntityFields(ChildClass.class, includeStatic);

        assertThat(actualFields)
                .hasSize(expectedFieldNames.length)
                .extracting(Field::getName)
                .containsExactlyInAnyOrder(expectedFieldNames);
    }

    static class ParentClass extends ModifiableEntity {
        private static String staticField;
        private String parentField;
    }

    static class ChildClass extends ParentClass {
        private String childField;
    }

    @ParameterizedTest(name = "Get unproxied class from class name: {0}")
    @CsvSource({
            "ch.ti8m.egov.framework.persistence.util.ReflectionUtilsTest$ChildClass, ch.ti8m.egov.framework.persistence.util.ReflectionUtilsTest$ChildClass",
            "ch.ti8m.egov.framework.persistence.util.ReflectionUtilsTest$ChildClass$$Proxied, ch.ti8m.egov.framework.persistence.util.ReflectionUtilsTest$ChildClass",
            "ch.ti8m.egov.framework.persistence.util.ReflectionUtilsTest, ch.ti8m.egov.framework.persistence.util.ReflectionUtilsTest",
            "ch.ti8m.egov.framework.persistence.util.ReflectionUtilsTest$$Proxied, ch.ti8m.egov.framework.persistence.util.ReflectionUtilsTest",
    })
    void fromClassName(final String className, final String expectedClassName) throws ClassNotFoundException {
        final Class<?> expectedClass = Class.forName(expectedClassName);

        final Class<?> actualClass = ReflectionUtils.fromClassName(className, this.getClass().getClassLoader());

        assertThat(actualClass).isEqualTo(expectedClass);
    }

    @ParameterizedTest(name = "Throw error upon empty className: {0}")
    @NullAndEmptySource
    void fromClassName_nullAndEmpty(final String className) throws ClassNotFoundException {
        try {
            ReflectionUtils.fromClassName(className, this.getClass().getClassLoader());
        } catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("className cannot be null or empty");
        }
    }
}