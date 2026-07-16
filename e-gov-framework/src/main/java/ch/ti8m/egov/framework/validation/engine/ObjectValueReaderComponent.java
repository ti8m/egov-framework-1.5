package ch.ti8m.egov.framework.validation.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class ObjectValueReaderComponent {

    public static final String KEY = "[key]";
    public static final String INDEX = "[i]";
    private static final String ROOT = "root";
    private static final String AGGREGATE = "aggregate";
    private static final String NULL = "NULL";
    private final FieldValueExtractorComponent fieldValueExtractorComponent;

    @Autowired
    public ObjectValueReaderComponent(FieldValueExtractorComponent fieldValueExtractorComponent) {
        this.fieldValueExtractorComponent = fieldValueExtractorComponent;
    }

    public Object getValue(
            final Object validationObject,
            final Object businessObject,
            final ValidationMethodMapper validationMethodMapper,
            final Integer[] indexes,
            final String[] keys
    ) {
        if (validationObject instanceof String) {
            if (validationObject.equals(NULL)) {
                return null;
            } else if (((String) validationObject).startsWith(ROOT)) {
                return getObjectValue(Arrays.asList(((String) validationObject).split("\\.")), businessObject,
                        new LinkedList<>(Arrays.asList(indexes)), new LinkedList<>(Arrays.asList(keys)), (String) validationObject);
            } else if (((String) validationObject).startsWith(AGGREGATE)) {
                return getObjectValue(Arrays.asList(((String) validationObject).split("\\.")),
                        validationMethodMapper.getAggregate(), new LinkedList<>(Arrays.asList(indexes)),
                        new LinkedList<>(Arrays.asList(keys)), (String) validationObject);
            }
        }
        return validationObject;
    }

    private Object getObjectValue(
            final List<String> fieldPath,
            final Object businessObject,
            final Queue<Integer> indexes,
            final Queue<String> keys,
            final String fieldPathDebug
    ) {
        final String nextPathElement;
        if (fieldPath.get(0).equals(KEY)) {
            if (keys.isEmpty()) {
                return null;
            }
            nextPathElement = keys.remove();
        } else {
            nextPathElement = fieldPath.get(0).split("\\" + INDEX)[0];
        }

        if ((nextPathElement.equals(ROOT) || nextPathElement.equals(AGGREGATE)) && fieldPath.size() == 1) {
            if (fieldPath.get(0).endsWith(INDEX)) {
                if (indexes.isEmpty()) {
                    return null;
                }
                return ((List<?>) businessObject).get(indexes.remove());
            }

            return businessObject;
        }

        if (nextPathElement.equals(ROOT) || nextPathElement.equals(AGGREGATE)) {
            return getObjectValue(fieldPath.subList(1, fieldPath.size()), businessObject, indexes, keys, fieldPathDebug);
        }

        final Object adaptedBusinessObject;
        if (businessObject instanceof List) {
            if (indexes.isEmpty()) {
                return null;
            }
            adaptedBusinessObject = ((List<?>) businessObject).get(indexes.remove());
            if (fieldPath.size() == 1 && fieldPath.get(0).endsWith(INDEX)) {
                return adaptedBusinessObject;
            }
        } else {
            adaptedBusinessObject = businessObject;
        }

        final Object declaredField = fieldValueExtractorComponent.getDeclaredField(nextPathElement, adaptedBusinessObject, fieldPathDebug);

        final List<String> nextFieldPath = fieldPath.size() > 1
                ? fieldPath.subList(1, fieldPath.size())
                : fieldPath;

        return fieldPath.size() == 1 && !fieldPath.get(0).endsWith(INDEX)
                ? declaredField
                : getObjectValue(nextFieldPath, declaredField, indexes, keys, fieldPathDebug);
    }
}
