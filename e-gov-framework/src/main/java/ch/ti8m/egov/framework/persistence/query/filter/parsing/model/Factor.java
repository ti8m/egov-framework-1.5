package ch.ti8m.egov.framework.persistence.query.filter.parsing.model;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.persistence.query.JoinConfiguration;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.join.TableJoins;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.tokenizer.Token;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.util.FilterOperatorUtil;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.util.JoinUtil;
import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.ti8m.egov.framework.persistence.query.QueryBuilder.IN_CONDITION_LIMIT;

@Slf4j
@Builder
public class Factor {

    private static final int FIELD_GROUP = 2;
    private static final int OPERATOR_GROUP = 4;
    private static final int VALUE_GROUP = 5;

    private Optional<Expression> expression;
    private Optional<String> filter;

    private static boolean isBooleanType(final String value) {
        return Boolean.TRUE.toString().equals(value.toLowerCase()) || Boolean.FALSE.toString().equals(value.toLowerCase());
    }

    public Triple<String, List<Object>, TableJoins> toSqlString(
            final Class<? extends ModifiableEntity> clazz,
            final List<Object> parameters,
            final TableJoins joins,
            final NameTranslationComponent nameTranslationComponent,
            final JoinConfiguration joinConfiguration) {

        final Triple<String, List<Object>, TableJoins> sqlExpression;
        if (expression.isPresent()) {
            sqlExpression = expression.get().toSqlString(clazz, parameters, joins, nameTranslationComponent, joinConfiguration);
        } else {
            sqlExpression = Triple.of("", parameters, joins);
        }
        final String sqlFilter;
        if (filter.isPresent()) {
            final Pattern factorMatcher = Pattern.compile(Token.Type.FILTER.getRegex());
            final Matcher matcher = factorMatcher.matcher(filter.get());
            if (!matcher.find()) {
                throw new EGovException(ExceptionCode.COMPILER_ERROR, "unable to match factor to filter");
            }
            final Pair<String, TableJoins> join = JoinUtil.getJoin(
                    clazz,
                    new LinkedList<>(Arrays.asList(matcher.group(FIELD_GROUP).split("\\."))),
                    new TableJoins(),
                    nameTranslationComponent,
                    true,
                    joinConfiguration);

            final String value = matcher.group(VALUE_GROUP).trim();
            final String operator = FilterOperatorUtil.getOperator(matcher.group(OPERATOR_GROUP).trim().replaceAll("\\s+", " "));
            if ("NULL".equals(value)) {
                sqlFilter = join.getLeft() + " " + operator + " NULL";
            } else if ("NOT NULL".equals(value)) {
                sqlFilter = join.getLeft() + " " + operator + " NOT NULL";
            } else if ("IN".equals(operator) || "NOT IN".equals(operator)) {
                final String itemsString = value.substring(1, value.length() - 1); // remove surrounding braces
                final List<String> items = Arrays.asList(itemsString.split(",\\s*"));

                if (items.stream().allMatch(this::isNumeric)) {
                    sqlFilter = join.getLeft() + " " + operator + " " + value;
                } else {
                    sqlFilter = join.getLeft() + " " + operator + " (?" + (",?".repeat(items.size() - 1)) + ")";
                    items.stream().map(this::parseValue).forEach(parameters::add);
                }

                logLimitExceededIfNeeded(items, join.getLeft() + " " + operator);
            } else if ("CONTAINS".equals(operator)) {
                sqlFilter = "CONTAINS(" + join.getLeft() + ", ?)";
                parameters.add(value.substring(1, value.length() - 1)); // remove surrounding "'"
            } else {
                sqlFilter = join.getLeft() + " " + operator + " ?";
                final Object param = parseValue(value);
                parameters.add(param);
            }
            joins.putAll(join.getRight());
            addJoins(joins, join.getRight());
        } else {
            sqlFilter = "";
        }
        return Triple.of(
                "(" + sqlExpression.getLeft() + sqlFilter + ")",
                parameters,
                joins
        );
    }

    private Object parseValue(final String value) {
        if (isSqlStringType(value)) {
            return value.substring(1, value.length() - 1); // remove surrounding "'"
        }
        if (isBooleanType(value)) {
            return Boolean.valueOf(value);
        }
        try {
            return Long.valueOf(value);
        } catch (final Exception ignored) {
        }
        try {
            return LocalDate.parse(value);
        } catch (final Exception ignored) {
        }
        try {
            return ZonedDateTime.parse(value);
        } catch (final Exception ignored) {
        }
        try {
            return LocalDateTime.parse(value);
        } catch (final Exception ignored) {
        }
        throw new EGovException(ExceptionCode.ILLEGAL_FILTER_PARAM_VALUE, "Value " + value + " not supported as value for filter param");
    }

    private boolean isNumeric(final String value) {
        try {
            Long.valueOf(value);
            return true;
        } catch (final Exception ignored) {
            return false;
        }
    }

    private boolean isSqlStringType(final String value) {
        return value.startsWith("'") && value.endsWith("'");
    }

    private void addJoins(
            final TableJoins joins,
            final TableJoins additionalJoins
    ) {
        additionalJoins.forEach(joins::putIfAbsent);
    }

    private void logLimitExceededIfNeeded(final List<String> inValues, final String filterBeforeInValues) {
        if (inValues.size() <= IN_CONDITION_LIMIT) {
            return;
        }

        final String previewValues = String.join(", ", inValues.subList(0, 3));
        final String filterPreview = filterBeforeInValues + " (" + previewValues + ", ...)";

        String mayExceedDbLimit = "";
        if (inValues.stream().anyMatch(value -> !isNumeric(value))) {
            mayExceedDbLimit = " More importantly, the Database could reject the resulting query when the parameter limit is reached!";
        }

        log.warn("Size of numeric values for IN Condition exceeds {}" +
                        " (was {}). This can lead to performance issues or Stackoverflow" +
                        "{}" +
                        " Current filter statement (may not be complete): {}",
                IN_CONDITION_LIMIT,
                inValues.size(),
                mayExceedDbLimit,
                filterPreview);
    }

}
