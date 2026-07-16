package ch.ti8m.egov.framework.persistence.query.filter.parsing.tokenizer;


import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class Tokenizer {

    public Queue<Token> tokenize(final String input) {
        final Map<Token.Type, Pattern> tokenPatterns = Arrays.stream(Token.Type.values())
                .map(type -> new AbstractMap.SimpleEntry<>(
                        type,
                        Pattern.compile(type.getRegex())
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        final Queue<Token> tokens = new LinkedList<>();
        int index = 0;
        while (index < input.length()) {
            final int filterIndex = index;
            final Token token = tokenPatterns.entrySet()
                    .stream()
                    .map(typePatternEntry -> {
                        final Matcher matcher = typePatternEntry.getValue().matcher(input.substring(filterIndex));
                        // TODO matcher.find() throws stack overflow for large conditions, like IN conditions with ~1300 values
                        return matcher.find()
                                ? Token.builder()
                                .type(typePatternEntry.getKey())
                                .value(matcher.group(1))
                                .length(matcher.group().length())
                                .build()
                                : null;
                    })
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow(() -> new EGovException(
                            ExceptionCode.COMPILER_ERROR,
                            "filter doesn't match any token",
                            Map.of("filter", input.substring(filterIndex))
                    ));
            tokens.add(token);
            index += token.getLength();
        }
        return tokens;
    }

}
