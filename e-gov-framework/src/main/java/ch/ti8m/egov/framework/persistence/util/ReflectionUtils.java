package ch.ti8m.egov.framework.persistence.util;

import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static List<Field> getAllEntityFields(final Class<?> clazz) {
        return getAllEntityFields(clazz, false);
    }

    public static List<Field> getAllEntityFields(final Class<?> clazz, final boolean includeStatic) {
        final List<Field> result = new ArrayList<>();

        Class<?> currentClass = clazz;

        while (currentClass != null) {
            final List<Field> filteredFields = Arrays.stream(currentClass.getDeclaredFields())
                    .filter(field -> includeStatic || isNotStatic(field))
                    .toList();

            result.addAll(filteredFields);

            currentClass = currentClass.getSuperclass();
        }

        return result;
    }

    public static boolean isNotStatic(final Member member) {
        return !Modifier.isStatic(member.getModifiers());
    }

    public static Class<?> fromClassName(final String className, final ClassLoader classLoader) throws ClassNotFoundException {
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("className cannot be null or empty");
        }

        final int proxyIndex = className.indexOf("$$");
        if (proxyIndex == -1) {
            return ClassUtils.forName(className, classLoader);
        }

        return ClassUtils.forName(className.substring(0, proxyIndex), classLoader);
    }
}