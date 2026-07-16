package ch.ti8m.egov.framework.validation.engine;

import ch.ti8m.egov.framework.validation.engine.conversion.Convert;
import ch.ti8m.egov.framework.validation.engine.conversion.Converter;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Component
public class FieldValueExtractorComponent implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public Object getDeclaredField(final String field, Object object, final String fieldPath) {
        if (object instanceof Map) {
            if (!((Map<String, Object>) object).containsKey(field)) {
                return null;
            }
            return ((Map<String, Object>) object).get(field);
        }
        if (object instanceof HibernateProxy hibernateProxy) {
            object = initializeByHibernateAndUnwrap(hibernateProxy, fieldPath, field);
        }

        try {
            final AccessibleObject declaredField = findDeclaredAccessibleObject(object.getClass(), field);
            Object fieldValue = null;

            if (declaredField instanceof Field) {
                declaredField.setAccessible(true);
                fieldValue = ((Field) declaredField).get(object);
            } else if (declaredField instanceof Method) {
                declaredField.setAccessible(true);
                fieldValue = invokeComputedMethod((Method) declaredField, object);
            }

            if (Objects.isNull(fieldValue)) {
                return fieldValue;
            }
            if (declaredField.isAnnotationPresent(ToMap.class)) {
                return toMap(fieldValue, declaredField, fieldPath);
            }
            if (declaredField.isAnnotationPresent(ToPrimitiveList.class)) {
                return toPrimitveList(fieldValue, declaredField, fieldPath);
            }
            if (declaredField.isAnnotationPresent(Convert.class)) {
                return convert(fieldValue, declaredField);
            }
            return fieldValue;

        } catch (final IllegalAccessException e) {
            if (log.isDebugEnabled()) {
                log.debug("Field {} not found in {}", field, fieldPath);
            }
            return null;
        } catch (final NullPointerException e) {
            throw new ValidationException("object not found, while looking for field: " + fieldPath);
        }
    }

    /**
     * Initializes (fetches from DB) the value of a hibernate proxy (like a field annotated with @ManyToOne(fetch=FetchType.LAZY)).
     * This is needed because we access fields reflectively and this does not cause the hibernate proxy to fetch the entity from DB,
     * leaving us with an uninitialized entity having uninitialized fields
     *
     * @param hibernateProxy the entity proxy
     * @return the initialized entity
     */
    private Object initializeByHibernateAndUnwrap(HibernateProxy hibernateProxy, String fieldPath, String field) {
        try {
            Hibernate.initialize(hibernateProxy);
            return hibernateProxy.getHibernateLazyInitializer().getImplementation();
        } catch (EntityNotFoundException e) {
            log.debug("""
                    {}
                    Cause: Entity was not found.
                    This can be caused by an expected but missing foreign key constraint.
                    We accept this and use the uninitialized proxy to access uninitialized values
                    """, getDefaultHibernateInitializationErrorMessage(fieldPath, field), e);
            return hibernateProxy; // keep using the proxy, accessing uninitialized values
        } catch (LazyInitializationException e) {
            throw new ValidationException("""
                    %s
                    Cause is LazyInitializationException.
                    Possible fixes:
                     - let a hibernate session/transaction run up until this point
                     - initialize the value within a session/transaction in advance
                    Original Error Message:
                    %s
                    """.formatted(getDefaultHibernateInitializationErrorMessage(fieldPath, field), e.getMessage()));
        } catch (Exception e) {
            throw new ValidationException("""
                    %s
                    Original Error Message:
                    %s
                    """.formatted(getDefaultHibernateInitializationErrorMessage(fieldPath, field), e.getMessage()));
        }
    }

    private String getDefaultHibernateInitializationErrorMessage(String fieldPath, String field) {
        return """
                 Field path "%s": parent of the field "%s" could not be initialized by hibernate!
                 This causes uninitialized values for this fieldPath.
                 Hibernate proxies must be initialized before accessed with reflection,
                 else all fields of the object are null/uninitialized.
                """
                .formatted(fieldPath, field);
    }

    private Object invokeComputedMethod(final Method method, final Object object) {
        try {
            return method.invoke(object);
        } catch (final IllegalAccessException e) {
            throw new ValidationException("Error while invoking ComputedProperty method: " + method.getName() + " on object: " + object.getClass().getName() + " msg:" + e.getMessage());
        } catch (final InvocationTargetException e) {
            throw new ValidationException("Error while invoking ComputedProperty method: " + method.getName() + " on object: " + object.getClass().getName() + " msg:" + e.getTargetException().getMessage());
        }
    }

    private AccessibleObject findDeclaredAccessibleObject(final Class<?> current, final String fieldName) {
        if (current == null) {
            return null;
        }

        return getAccessibleObjects(current).stream()
                .filter(accessibleObject -> Objects.equals(getDeclaredAccesibleObjectName(accessibleObject), fieldName))
                .findFirst()
                .orElseGet(() -> findDeclaredAccessibleObject(current.getSuperclass(), fieldName));
    }

    private List<AccessibleObject> getAccessibleObjects(final Class<?> current) {
        final List<AccessibleObject> accessibleObjects = new ArrayList<>();
        accessibleObjects.addAll(Arrays.stream(current.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(ComputedProperty.class))
                .toList());
        accessibleObjects.addAll(Arrays.asList(current.getDeclaredFields()));

        return accessibleObjects;
    }

    private String getDeclaredAccesibleObjectName(final AccessibleObject accessibleObject) {
        if (accessibleObject.isAnnotationPresent(ComputedProperty.class)) {
            return accessibleObject.getAnnotation(ComputedProperty.class).name();
        } else if (accessibleObject instanceof Field) {
            return ((Field) accessibleObject).getName();
        }
        return null;
    }

    private Object convert(final Object fieldValue, final AccessibleObject declaredField) {
        final Class<? extends Converter> converterClass = declaredField.getAnnotation(Convert.class).converter();
        return applicationContext.getBean(converterClass).execute(fieldValue);
    }

    private Map<Object, Object> toMap(final Object fieldValue, final AccessibleObject declaredField, final String fieldPath) {
        if (fieldValue instanceof Collection) {
            final String keyFieldName = declaredField.getAnnotation(ToMap.class).keyField();
            final Map<Object, Object> resultMap = new HashMap<>();
            ((Collection<?>) fieldValue).forEach(entry -> {
                final Object key = getDeclaredField(keyFieldName, entry, fieldPath);
                resultMap.put(key, entry);
            });
            return resultMap;
        } else {
            throw new ValidationException("Field annotated as @ToMap but is not a Collection: " + fieldPath);
        }
    }

    private List<Object> toPrimitveList(final Object fieldValue, final AccessibleObject declaredField, final String fieldPath) {
        if (fieldValue instanceof Collection) {
            final String primitiveField = declaredField.getAnnotation(ToPrimitiveList.class).primitiveField();
            final List<Object> resultMap = new ArrayList<>();
            ((Collection<?>) fieldValue).forEach(entry -> {
                final Object field = getDeclaredField(primitiveField, entry, fieldPath);
                resultMap.add(field);
            });
            return resultMap;
        } else {
            throw new ValidationException(
                    "Field annotated as @ToPrimitiveList but is not a Collection: " + fieldPath);
        }
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        FieldValueExtractorComponent.applicationContext = applicationContext;
    }
}
