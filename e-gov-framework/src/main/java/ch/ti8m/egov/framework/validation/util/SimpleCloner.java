package ch.ti8m.egov.framework.validation.util;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

public final class SimpleCloner {

    private SimpleCloner() {
    }

    public static <T> T getClone(final T original) {
        final Class<? extends T> clazz = (Class<? extends T>) original.getClass();
        try {
            final T clone = clazz.getDeclaredConstructor().newInstance();
            for (final Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Id.class)
                        || !isSimpleType(field.getType())
                        || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                field.set(clone, field.get(original));
            }
            return clone;
        } catch (final InstantiationException
                       | IllegalAccessException
                       | InvocationTargetException
                       | NoSuchMethodException e) {
            throw new EGovException(ExceptionCode.CANNOT_CLONE_OBJECT, "unable to clone object of type: " + clazz.getSimpleName());
        }
    }

    public static boolean isSimpleType(final Class<?> type) {
        return Integer.class.isAssignableFrom(type)
                || int.class.isAssignableFrom(type)
                || Long.class.isAssignableFrom(type)
                || long.class.isAssignableFrom(type)
                || Double.class.isAssignableFrom(type)
                || double.class.isAssignableFrom(type)
                || Float.class.isAssignableFrom(type)
                || float.class.isAssignableFrom(type)
                || LocalDate.class.isAssignableFrom(type)
                || LocalDateTime.class.isAssignableFrom(type)
                || LocalTime.class.isAssignableFrom(type)
                || String.class.isAssignableFrom(type)
                || Boolean.class.isAssignableFrom(type)
                || boolean.class.isAssignableFrom(type)
                || Locale.class.isAssignableFrom(type)
                || Enum.class.isAssignableFrom(type);
    }

}
