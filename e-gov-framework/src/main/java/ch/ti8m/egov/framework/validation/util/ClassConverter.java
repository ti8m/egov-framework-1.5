package ch.ti8m.egov.framework.validation.util;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.validation.engine.ToMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public final class ClassConverter {

    public static final String ID_FIELD = "ID_FIELD";

    private ClassConverter() {
    }

    //Overloading
    public static <T> T forClass(final Class<T> clazz, final Map<String, Object> entity, final boolean failSilent,
                                 final boolean checkIds) {
        return forClass(clazz, entity, null, failSilent, checkIds);
    }

    public static <T> T forClass(final Class<T> clazz, final Map<String, Object> entity) {
        return forClass(clazz, entity, null, false, false);
    }

    public static <T> T forClass(final Class<T> clazz, final Object entity) {
        return forClass(clazz, (Map<String, Object>) entity, null, false, false);
    }

    public static <T> T forClass(final Class<T> clazz, final Map<String, Object> entity, final T existingInstance) {
        return forClass(clazz, entity, existingInstance, false, true);
    }

    public static <T> T forClass(final Class<T> clazz, final Object entity, final T existingInstance) {
        return forClass(clazz, (Map<String, Object>) entity, existingInstance, false, true);
    }

    public static <T> T forClass(final TypeReference<T> typeReference, final Object value,
                                 final T oldValues) {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(typeReference);
        final Class<T> cls = (Class<T>) type.getRawClass();
        if (cls.isAssignableFrom(List.class)) {
            final Class listElementType = (Class) ((ParameterizedType) typeReference.getType()).getActualTypeArguments()[0];
            return (T) forListOfClass(listElementType, (List<Map<String, Object>>) value, (List<Object>) oldValues);
        } else {
            return forClass(cls, (Map<String, Object>) value, oldValues);
        }
    }

    public static <T> T forClass(final TypeReference<T> typeReference, final Object value) {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(typeReference);
        final Class<T> cls = (Class<T>) type.getRawClass();
        if (cls.isAssignableFrom(List.class)) {
            final Class listElementType = (Class) ((ParameterizedType) typeReference.getType()).getActualTypeArguments()[0];
            return (T) forListOfClass(listElementType, (List<Map<String, Object>>) value);
        } else {
            return forClass(cls, value);
        }
    }

    public static <T> T forClass(final Class<T> clazz, final Map<String, Object> entity, final T existingInstance,
                                 final boolean failSilent, final boolean checkIds) {
        try {
            final T instance = existingInstance == null ? (T) Arrays.stream(clazz.getConstructors())
                    .filter(constructor -> constructor.getParameterCount() == 0)
                    .findFirst()
                    .orElseThrow(() -> new EGovException(ExceptionCode.DEFAULT, "no zero arg constructor found for: " + clazz.getName()))
                    .newInstance() : existingInstance;
            if (Objects.isNull(entity)) {
                return null;
            }
            entity.forEach((fieldName, fieldValue) -> {
                try {
                    final Field field = FieldUtils.getField(clazz, fieldName, true);
                    if (Objects.isNull(field)) {
                        fieldNotAccessibleError(true, fieldName, clazz.getName());
                        return;
                    }
                    if (isSimpleType(field.getType())) {
                        handleSimpleType(instance, fieldValue, field, failSilent);
                        return;
                    }
                    if (field.getType().isEnum()) {
                        handleEnum(field, fieldValue, instance, failSilent);
                        return;
                    }
                    if (field.getType().isAssignableFrom(Class.class)) {
                        handleClass(field, fieldValue, instance, failSilent);
                        return;
                    }
                    if (field.getType().isAssignableFrom(List.class)) {
                        handleList(clazz, instance, field, fieldValue, failSilent, checkIds);
                        return;
                    }
                    if (field.getType().isAssignableFrom(fieldValue.getClass())) {
                        FieldUtils.writeField(field, instance, fieldValue, true);
                        return;
                    }
                    handleObject(field, (Map<String, Object>) fieldValue, instance, failSilent, checkIds);
                } catch (final IllegalAccessException e) {
                    fieldNotAccessibleError(failSilent, fieldName, clazz.getName());
                }
            });
            return instance;

        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
            instantiationFailed(clazz, failSilent);
        }
        return null;
    }

    private static <T> void handleClass(final Field field, final Object fieldValue, final T instance, final boolean failSilent) {
        try {
            final Class<?> aClass = Class.forName((String) fieldValue);
            field.set(instance, aClass);
        } catch (final ClassNotFoundException | IllegalAccessException e) {
            customError(failSilent, "could not set class: " + fieldValue + " for field: " + field.getName());
        }
    }

    private static <T> void handleSimpleType(final T instance,
                                             final Object fieldValue,
                                             final Field field, final boolean failSilent) throws IllegalAccessException {

        if (field.getType().isAssignableFrom(Object.class)) {
            if (Objects.isNull(fieldValue)) {
                field.set(instance, null);
                return;
            }
            FieldUtils.writeField(field, instance, fieldValue, true);
            return;
        }
        if (field.getType().isAssignableFrom(Locale.class)) {
            if (Objects.isNull(fieldValue)) {
                field.set(instance, null);
                return;
            }
            FieldUtils.writeField(field, instance, new Locale(fieldValue.toString()));
            return;
        }
        if (Objects.isNull(fieldValue)) {
            FieldUtils.writeField(field, instance, fieldValue);
            return;
        }
        if (ClassUtils.isAssignable(fieldValue.getClass(), field.getType())) {
            FieldUtils.writeField(field, instance, fieldValue);
            return;
        }
        if (field.getType() == LocalDateTime.class && fieldValue instanceof LocalDate) {
            FieldUtils.writeField(field, instance, ((LocalDate) fieldValue).atStartOfDay());
            return;
        }
        if (fieldValue.equals("")) {
            FieldUtils.writeField(field, instance, null);
            return;
        }
        //Try to parse String to class in case of Type Mismatch
        try {
            FieldUtils.writeField(field, instance, MethodUtils.invokeStaticMethod(field.getType(), "parse",
                    fieldValue.toString()));
        } catch (final NoSuchMethodException | InvocationTargetException e) {
            customError(failSilent, "Failed to parse field : " + field.getName() + " of type: " + field.getType() + " from String");
        }

    }

    private static <T> void handleObject(final Field field, final Map<String, Object> fieldValue, final T instance,
                                         final boolean failSilent,
                                         final boolean checkIds) throws IllegalAccessException {
        final Object nextInstance = ClassConverter.forClass((Class<T>) field.getType(),
                fieldValue,
                (T) FieldUtils.readField(field, instance),
                failSilent, checkIds);
        FieldUtils.writeField(field, instance, nextInstance);
    }

    private static <T> void handleEnum(final Field field, final Object fieldValue, final T instance, final boolean failSilent) throws IllegalAccessException {
        try {
            if (fieldValue != null && !((String) fieldValue).isEmpty()) {
                final Object value = MethodUtils.invokeStaticMethod(field.getType(), "valueOf", fieldValue);
                FieldUtils.writeField(field, instance, value);
            } else {
                FieldUtils.writeField(field, instance, null);
            }
        } catch (final ReflectiveOperationException e) {
            extractedEnumFailed(failSilent, field);
        }

    }

    private static <T, S> void handleList(final Class<T> clazz, final T instance, final Field listField,
                                          final Object listFieldValue,
                                          final boolean failSilent,
                                          final boolean checkIds) throws IllegalAccessException {
        final Class<S> listElementType = (Class<S>) ((ParameterizedType) listField.getGenericType()).getActualTypeArguments()[0];
        final List<S> oldValues = (List<S>) FieldUtils.readField(listField, instance, true);
        final List<S> values;
        if (isSimpleType(listElementType)) {
            values = (List<S>) listFieldValue;
        } else if (listField.isAnnotationPresent(ToMap.class)) {
            final String keyField = listField.getAnnotation(ToMap.class).keyField();
            values = forToMap(listElementType, (Map<String, Map<String, Object>>) listFieldValue, oldValues, keyField,
                    failSilent, checkIds);
        } else if (listElementType.isAnnotationPresent(Entity.class)) {
            values = forListOfClass(listElementType, (List<Map<String, Object>>) listFieldValue, oldValues, ID_FIELD,
                    failSilent,
                    checkIds);

        } else {
            values = forListOfClass(listElementType, (List<Map<String, Object>>) listFieldValue, oldValues, "",
                    failSilent,
                    checkIds);
        }
        if (listElementType.isAnnotationPresent(Entity.class)) {
            handelBackpointers(clazz, instance, values, listElementType, failSilent);
        }
        FieldUtils.writeField(listField, instance, values);
    }

    private static <T> void checkListElementIds(final Class<?> listElementType, final List<?> values,
                                                final List<?> oldValues, final boolean failSilent) {
        final Field idField = getIdField(listElementType, failSilent);
        final List<Integer> oldIds = new ArrayList<>();
        try {
            for (final Object oldEntry : oldValues) {
                oldIds.add((Integer) FieldUtils.readField(idField, oldEntry, true));
            }
            for (final Object listItem : values) {
                final Integer id = (Integer) FieldUtils.readField(idField, listItem, true);
                if (!oldIds.contains(id)) {
                    FieldUtils.writeField(idField, listItem, null);
                }
            }
        } catch (
                final IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static <T> List<T> forListOfClass(final Class<T> listElementType, final List<Map<String, Object>> list) {
        return forListOfClass(listElementType, list, null, ID_FIELD, false, false);
    }

    public static <T> List<T> forListOfClass(final Class<T> listElementType, final List<Map<String, Object>> list,
                                             final List<T> oldValues) {
        return forListOfClass(listElementType, list, oldValues, ID_FIELD, false, true);
    }

    public static <T> List<T> forListOfClass(final Class<T> listElementType, final List<Map<String, Object>> list,
                                             final List<T> oldValues, final String comparisonFieldName, final boolean failSilent,
                                             final boolean checkIds) {

        final List<T> values;
        if (Objects.nonNull(oldValues) && !oldValues.isEmpty()) {
            values = list.stream()
                    .map(listEntry -> ClassConverter.forClass(listElementType, listEntry,
                            findOldEntryByField(listElementType, listEntry, oldValues,
                                    comparisonFieldName, failSilent), failSilent,
                            checkIds))
                    .collect(Collectors.toList());
        } else {
            values = list.stream()
                    .map(listEntry -> ClassConverter.forClass(listElementType, listEntry, null, failSilent, checkIds))
                    .collect(Collectors.toList());
        }
        if (checkIds) {
            checkListElementIds(listElementType, values, oldValues, failSilent);
        }
        if (Objects.nonNull(oldValues)) {
            oldValues.clear();
            oldValues.addAll(values);
            return oldValues;
        } else {
            return values;
        }
    }

    private static <T> T findOldEntryByField(final Class<T> listElementType, final Map<String, Object> entity,
                                             final List<T> oldValues,
                                             final String comparisonFieldName, final boolean failSilent) {
        final String finalComparisonFieldName;
        if (comparisonFieldName.isEmpty()) {
            return null;
        }
        if (comparisonFieldName.equals(ID_FIELD)) {
            finalComparisonFieldName = getIdField(listElementType, failSilent).getName();
        } else {
            finalComparisonFieldName = comparisonFieldName;
        }

        if (Objects.isNull(FieldUtils.getField(listElementType, finalComparisonFieldName, true))) {
            fieldNotFoundError(listElementType, "ComparisonField", finalComparisonFieldName, failSilent);
            return null;
        }
        if (Objects.isNull(entity.get(finalComparisonFieldName))) {
            return null;
        }
        return oldValues.stream().
                filter(oldEntry -> {
                    try {
                        return entity.get(finalComparisonFieldName).equals(FieldUtils.readField(oldEntry, finalComparisonFieldName, true));
                    } catch (final IllegalAccessException e) {
                        fieldNotAccessibleError(failSilent, finalComparisonFieldName, listElementType.getName());
                    }
                    return false;
                }).
                findFirst().
                orElse(null);

    }

    private static <T> void handelBackpointers(final Class<T> clazz, final T instance, final List<?> values,
                                               final Class<?> listElementType,
                                               final boolean failSilent) {
        final Field backPointerField = getBackPointerField(listElementType, clazz);
        if (Objects.nonNull(backPointerField)) {
            values.forEach(listEntry -> {
                try {
                    FieldUtils.writeField(backPointerField, listEntry, instance, true);
                } catch (final IllegalAccessException e) {
                    fieldNotAccessibleError(failSilent, backPointerField.getName(), listElementType.getName());
                }
            });
        }
    }

    private static <T, S extends ModifiableEntity> List<T> forToMap(final Class<T> listElementType,
                                                                    final Map<String, Map<String, Object>> fieldValue,
                                                                    final List<T> oldValues,
                                                                    final String keyField,
                                                                    final boolean failSilent,
                                                                    final boolean checkIds) {

        final List<T> values;
        final Map<Map<String, Object>, T> oldValueMap = new HashMap<>();
        values = fieldValue.entrySet()
                .stream()
                .map(fieldValueEntry -> {
                    if (Objects.isNull(fieldValueEntry.getValue())) {
                        fieldValueEntry.setValue(new HashMap<>());
                    }
                    fieldValueEntry.getValue().put(keyField, fieldValueEntry.getKey());
                    final T oldValue;
                    if (Objects.nonNull(oldValues)) {
                        oldValue = getOldValueByKeyField(listElementType, oldValues, keyField, failSilent, fieldValueEntry);
                        if (ModifiableEntity.class.isAssignableFrom(listElementType)) {
                            fieldValueEntry.getValue()
                                    .putAll(getIdAndAuditFieldsForMap((Class<S>) listElementType,
                                            failSilent, listElementType, (S) oldValue));
                        }
                        oldValueMap.put(fieldValueEntry.getValue(), oldValue);
                    }
                    return fieldValueEntry.getValue();
                })
                .map(fieldValueEntry -> ClassConverter.forClass(listElementType, fieldValueEntry,
                        oldValueMap.get(fieldValueEntry), failSilent, checkIds))
                .collect(Collectors.toList());

        if (checkIds) {
            checkListElementIds(listElementType, values, oldValues, failSilent);
        }
        if (Objects.nonNull(oldValues)) {
            oldValues.clear();
            oldValues.addAll(values);
            return oldValues;
        } else {
            return values;
        }
    }

    private static <T> T getOldValueByKeyField(final Class<T> listElementType,
                                               final List<T> oldValues,
                                               final String keyField,
                                               final boolean failSilent,
                                               final Map.Entry<String, Map<String, Object>> stringMapEntry) {
        return oldValues.stream().filter(oldListElement -> {
            try {
                return FieldUtils.readField(oldListElement, keyField, true).equals(stringMapEntry.getKey());
            } catch (final IllegalAccessException e) {
                fieldNotAccessibleError(failSilent, keyField, listElementType.getName());
            }
            return false;
        }).findFirst().orElse(null);
    }


    private static <T extends ModifiableEntity> Map<String, Object> getIdAndAuditFieldsForMap(final Class<T> clazz,
                                                                                              final boolean failSilent,
                                                                                              final Class<?> listElementType,
                                                                                              final T oldValue) {
        final Map<String, Object> resultMap = new HashMap<>();
        if (Objects.nonNull(oldValue)) {
            try {
                final Field idField = getIdField(listElementType, failSilent);
                resultMap.put(idField.getName(), FieldUtils.readField(idField, oldValue, true));
                resultMap.put("createdBy", oldValue.getCreatedBy());
                resultMap.put("createdDate", oldValue.getCreatedDate());
                resultMap.put("modifiedBy", oldValue.getModifiedBy());
                resultMap.put("modifiedDate", oldValue.getModifiedDate());
            } catch (final IllegalAccessException e) {
                fieldNotAccessibleError(failSilent, "Id/Auditfields", clazz.getName());
            }
        }
        return resultMap;
    }

    private static <T> Field getBackPointerField(final Class<?> listElementType, final Class<T> clazz) {
        for (final Field field : listElementType.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(clazz)) {
                return field;
            }
        }
        return null;
    }

    private static <T> Field getIdField(final Class<T> clazz, final boolean failSilent) {
        final Field idField = FieldUtils.getFieldsListWithAnnotation(clazz, Id.class).stream().findFirst().orElse(null);
        if (Objects.isNull(idField)) {
            fieldNotFoundError(clazz, "Id Field", "", failSilent);
        }
        return idField;
    }

    private static boolean isSimpleType(final Class<?> type) {
        return !Enum.class.isAssignableFrom(type)
                && (SimpleCloner.isSimpleType(type)
                || type.isAssignableFrom(Map.class));
    }

    //Error Logging Helpers
    private static <T> void fieldNotFoundError(final Class<T> clazz, final String fieldDescription, final String fieldName,
                                               final boolean failSilent) {
        if (failSilent) {
            if (log.isDebugEnabled()) {
                log.debug(fieldDescription + " " + fieldName + " not found in class " + clazz.getName());
            }
        } else {
            throw new EGovException(ExceptionCode.DEFAULT, " unable to find field: " + fieldName + " in class:" + clazz.getSimpleName());
        }
    }


    private static <T> void instantiationFailed(final Class<T> clazz, final boolean failSilent) {
        if (failSilent) {
            if (log.isDebugEnabled()) {
                log.debug("Could not instantiate class " + clazz.getName());
            }
        } else {
            throw new EGovException(ExceptionCode.DEFAULT,
                    "[ClassConverter]: Could not instantiate class " + clazz.getName());
        }
    }

    private static void extractedEnumFailed(final boolean failSilent, final Field field) {
        if (failSilent) {
            if (log.isDebugEnabled()) {
                log.debug("Field " + field.getName() + " failed enum creation");
            }
        } else {
            throw new EGovException(ExceptionCode.DEFAULT,
                    "[ClassConverter]: Field " + field.getName() + " failed enum creation");
        }
    }

    private static <T> void fieldNotAccessibleError(final boolean failSilent, final String fieldName,
                                                    final String clazzName) {
        if (failSilent) {
            if (log.isDebugEnabled()) {
                log.debug("Field " + fieldName + " not accessible in class " + clazzName);
            }
        } else {
            throw new EGovException(ExceptionCode.DEFAULT,
                    "[ClassConverter]: Field " + fieldName + " not accessible in class " + clazzName);
        }
    }

    private static <T> void customError(final boolean failSilent, final String msg) {
        if (failSilent) {
            if (log.isDebugEnabled()) {
                log.debug(msg);
            }
        } else {
            throw new EGovException(ExceptionCode.DEFAULT, "[ClassConverter]: " + msg);
        }
    }

}
