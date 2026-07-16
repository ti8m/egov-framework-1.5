package ch.ti8m.egov.framework.persistence.query.filter.parsing.tokenizer;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Token {

    private static final String ZONED_DATE_TIME_REGEX = "(\\d{4}(-\\d\\d(-\\d\\d(T\\d\\d:\\d\\d(:\\d\\d)?(\\.\\d+)?(([+-]\\d\\d:\\d\\d)(\\[.*\\])?|Z)?)?)?))";
    private static final String DATE_REGEX = "([0-9]{4}-)[0-9]{2}-[0-9]{2}";
    private static final String BOOLEANS_REGEX = "true|false|TRUE|FALSE";
    private static final String NUMBERS_REGEX = "-?[0-9]+";
    private static final String FIELD_REGEX = "([a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*)";
    private static final String OPERATOR_REGEX = "(==|>=|<=|<|>|!=|\\s+IS\\s+NOT\\s+|\\s+IS\\s+|\\s+LIKE\\s+|\\s+NOT\\s+LIKE\\s+|\\s+IN\\s+|\\s+NOT\\s+IN\\s+|\\s+CONTAINS\\s+)";
    private static final String STRING_FILTER_REGEX = "((?<!\\\\)'.*?(?<!\\\\)'|(?:NOT\\s)?NULL)";
    private static final String LIST_FILTER_REGEX = "(\\((('[^']+')|(\\d+))(\\s*,\\s*(('[^']+')|(\\d+)))*\\))";
    private static final String FILTER_REGEX = "(" + ZONED_DATE_TIME_REGEX + "|" + DATE_REGEX + "|" + NUMBERS_REGEX + "|" + BOOLEANS_REGEX + "|" + STRING_FILTER_REGEX + "|" + LIST_FILTER_REGEX + ")";
    private Type type;
    private Object value;
    private int length;

    @Getter
    public enum Type {
        AND("^\\s*(\\b(a|A)(n|N)(d|D)\\b)\\s*"),
        OR("^\\s*(\\b(o|O)(r|R)\\b)\\s*"),
        OPENING_PARENTHESIS("^\\s*(\\()\\s*"),
        CLOSING_PARENTHESIS("^\\s*(\\))\\s*"),
        FILTER("^\\s*(" + FIELD_REGEX + "\\s*" + OPERATOR_REGEX + "\\s*" + FILTER_REGEX + ")\\s*");

        private final String regex;

        Type(final String regex) {
            this.regex = regex;
        }

    }

}
