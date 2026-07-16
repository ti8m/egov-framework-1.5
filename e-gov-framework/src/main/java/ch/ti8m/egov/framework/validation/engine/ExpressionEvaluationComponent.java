package ch.ti8m.egov.framework.validation.engine;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ExpressionEvaluationComponent {

    private static final String NOT = "NOT";
    private static final String EQUAL = "EQUAL";
    private static final String AND = "AND";
    private static final String OR = "OR";
    private static final String LESS_THAN = "LESS_THAN";
    private static final String LESS_EQUAL_THAN = "LESS_EQUAL_THAN";
    private static final String GREATER_THAN = "GREATER_THAN";
    private static final String GREATER_EQUAL_THAN = "GREATER_EQUAL_THAN";
    private static final String LENGTH = "LENGTH";
    private static final String MIN_MAX_LENGTH = "MIN_MAX_LENGTH";
    private static final String END_OF_DAY = "END_OF_DAY";
    private static final String START_OF_DAY = "START_OF_DAY";
    private static final String AS_DATE = "AS_DATE";
    private static final String AS_TIME = "AS_TIME";
    private static final String AS_DATETIME = "AS_DATETIME";
    private static final String CONTAINS = "CONTAINS";
    private static final String AS_LIST = "AS_LIST";
    private static final String IS_UPPERCASE = "IS_UPPERCASE";
    private static final String REGEX = "REGEX";

    private static final String AS_STRING = "AS_STRING";
    private static final LocalTime MAX_TIME = LocalTime.of(23, 59, 59);

    public Object evaluateExpression(final String[] operatorArray, final List<Object> parameters,
                                     final ValidationMethodMapper validationMethodMapper) {
        if (operatorArray[0].equals(NOT)) {
            return !(boolean) evaluateExpression(Arrays.copyOfRange(operatorArray, 1, operatorArray.length), parameters,
                    validationMethodMapper);
        }
        final Object result;
        switch (operatorArray[0]) {
            case EQUAL:
                result = parameters.stream().allMatch(parameter -> equals(parameter, parameters.get(0)));
                break;
            case AND:
                result = parameters.parallelStream().allMatch(booleanParameter -> (boolean) booleanParameter);
                break;
            case OR:
                result = parameters.parallelStream().anyMatch(booleanParameter -> (boolean) booleanParameter);
                break;
            case LESS_THAN:
                result = parameters.subList(1, parameters.size()).parallelStream().allMatch(parameter -> {
                    if (parameter instanceof Number) {
                        return Double.parseDouble(String.valueOf(parameters.get(0)))
                                < Double.parseDouble(String.valueOf(parameter));
                    } else if (parameter instanceof LocalDateTime) {
                        return ((LocalDateTime) parameters.get(0)).isBefore((LocalDateTime) parameter);
                    } else if (parameter instanceof LocalTime) {
                        return ((LocalTime) parameters.get(0)).isBefore((LocalTime) parameter);
                    } else if (parameter instanceof LocalDate) {
                        return ((LocalDate) parameters.get(0)).isBefore((LocalDate) parameter);
                    }
                    return false;
                });
                break;
            case LESS_EQUAL_THAN:
                result = parameters.subList(1, parameters.size()).parallelStream().allMatch(parameter -> {
                    if (parameter instanceof Number) {
                        return Double.parseDouble(String.valueOf(parameters.get(0)))
                                <= Double.parseDouble(String.valueOf(parameter));
                    } else if (parameter instanceof LocalDateTime) {
                        return !((LocalDateTime) parameters.get(0)).isAfter((LocalDateTime) parameter);
                    } else if (parameter instanceof LocalTime) {
                        return !((LocalTime) parameters.get(0)).isAfter((LocalTime) parameter);
                    } else if (parameter instanceof LocalDate) {
                        return !((LocalDate) parameters.get(0)).isAfter((LocalDate) parameter);
                    }
                    return false;
                });
                break;
            case GREATER_THAN:
                result = parameters.subList(1, parameters.size()).parallelStream().allMatch(parameter -> {
                    if (parameter instanceof Number) {
                        return Double.parseDouble(String.valueOf(parameters.get(0)))
                                > Double.parseDouble(String.valueOf(parameter));
                    } else if (parameter instanceof LocalDateTime) {
                        return ((LocalDateTime) parameters.get(0)).isAfter((LocalDateTime) parameter);
                    } else if (parameter instanceof LocalTime) {
                        return ((LocalTime) parameters.get(0)).isAfter((LocalTime) parameter);
                    } else if (parameter instanceof LocalDate) {
                        return ((LocalDate) parameters.get(0)).isAfter((LocalDate) parameter);
                    }
                    return false;
                });
                break;
            case GREATER_EQUAL_THAN:
                result = parameters.subList(1, parameters.size()).parallelStream().allMatch(parameter -> {
                    if (parameter instanceof Number) {
                        return Double.parseDouble(String.valueOf(parameters.get(0)))
                                >= Double.parseDouble(String.valueOf(parameter));
                    } else if (parameter instanceof LocalDateTime) {
                        return !((LocalDateTime) parameters.get(0)).isBefore((LocalDateTime) parameter);
                    } else if (parameter instanceof LocalTime) {
                        return !((LocalTime) parameters.get(0)).isBefore((LocalTime) parameter);
                    } else if (parameter instanceof LocalDate) {
                        return !((LocalDate) parameters.get(0)).isBefore((LocalDate) parameter);
                    }
                    return false;
                });
                break;
            case LENGTH:
                final Object parameter = parameters.get(0);
                if (parameter == null) {
                    result = -1;
                    break;
                }
                if (parameter instanceof Collection) {
                    result = ((Collection<?>) parameter).size();
                    break;
                }
                result = ((String) parameters.get(0)).length();
                break;
            case MIN_MAX_LENGTH:
                final int length = parameters.get(0) == null
                        ? -1
                        : parameters.get(0) instanceof final Integer intParam
                        ? intParam
                        : ((String) parameters.get(0)).length();

                final int min = parameters.get(1) instanceof final Integer intParam
                        ? intParam
                        : Integer.parseInt((String) parameters.get(1));
                final int max = parameters.get(2) instanceof final Integer intParam
                        ? intParam
                        : Integer.parseInt((String) parameters.get(2));
                result = length >= min && length <= max;
                break;
            case CONTAINS:
                final List<Object> containingList = (List<Object>) parameters.get(0);
                final Object value = parameters.get(1);
                result = containingList.contains(value);
                break;
            case AS_LIST:
                result = parameters;
                break;
            case END_OF_DAY:
                LocalDateTime endOfDayTemporal;
                try {
                    endOfDayTemporal = LocalDate.parse((String) parameters.get(0)).atTime(MAX_TIME);
                } catch (final DateTimeParseException e) {
                    endOfDayTemporal = LocalDateTime.parse((String) parameters.get(0)).toLocalDate().atTime(MAX_TIME);
                }
                result = endOfDayTemporal;
                break;
            case START_OF_DAY:
                LocalDateTime startOfDayTemporal;
                try {
                    startOfDayTemporal = LocalDate.parse((String) parameters.get(0)).atStartOfDay();
                } catch (final DateTimeParseException e) {
                    startOfDayTemporal = LocalDateTime.parse((String) parameters.get(0)).toLocalDate().atStartOfDay();
                }
                result = startOfDayTemporal;
                break;
            case AS_DATE:
                result = LocalDate.parse((String) parameters.get(0));
                break;
            case AS_TIME:
                result = LocalTime.parse((String) parameters.get(0));
                break;
            case AS_DATETIME:
                result = LocalDateTime.parse((String) parameters.get(0));
                break;
            case AS_STRING:
                if (parameters.get(0) instanceof Enum) {
                    result = ((Enum<?>) parameters.get(0)).name();
                } else {
                    result = String.valueOf(parameters.get(0));
                }
                break;
            case IS_UPPERCASE:
                result = parameters.get(0).equals(((String) parameters.get(0)).toUpperCase());
                break;
            case REGEX:
                result = Pattern.compile((String) parameters.get(0)).matcher((String) parameters.get(1)).find();
                break;
            default:
                result = validationMethodMapper.isValid(operatorArray[0], parameters);
        }
        return result;
    }

    private boolean equals(final Object o1, final Object o2) {
        if (o1 == null && o2 != null) {
            return false;
        }

        if (o1 instanceof Integer && o2 instanceof String
                || o1 instanceof String && o2 instanceof Integer) {
            return o1.toString().equals(o2.toString());
        }

        return o1 == null || o1.equals(o2);
    }

}
