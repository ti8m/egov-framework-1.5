package ch.ti8m.egov.framework.persistence.query.filter.parsing;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.Expression;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.ExpressionStrich;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.Factor;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.FilterAst;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.Term;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.TermStrich;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.tokenizer.Token;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.tokenizer.Tokenizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Queue;

@Slf4j
@Component
public class FilterParser {

    /*
        EXPRESSION -> TERM EXPRESSION'
        EXPRESSION' -> or TERM EXPRESSION'| e
        TERM -> FACTOR TERM'
        TERM' -> and FACTOR TERM' | e
        FACTOR -> "(" EXPRESSION ")" | filter
     */

    private final Tokenizer tokenizer;

    @Autowired
    public FilterParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public FilterAst parse(final String filterString) {
        final Queue<Token> tokens = tokenizer.tokenize(filterString);
        return FilterAst.builder()
                .expression(parseExpression(tokens).getLeft())
                .build();
    }

    private Pair<Expression, Integer> parseExpression(final Queue<Token> tokens) {
        final Pair<Term, Integer> term = parseTerm(tokens);
        final Optional<Pair<ExpressionStrich, Integer>> expressionStrich = parseExpressionStrich(tokens);
        return Pair.of(
                Expression.builder()
                        .term(term.getLeft())
                        .expressionStrich(expressionStrich.map(Pair::getLeft))
                        .build(),
                term.getRight() + expressionStrich.map(Pair::getRight).orElse(0)
        );
    }

    private Optional<Pair<ExpressionStrich, Integer>> parseExpressionStrich(final Queue<Token> tokens) {
        if (tokens.isEmpty() || !tokens.peek().getType().equals(Token.Type.OR)) {
            return Optional.empty();
        } else {
            final Token token = tokens.remove(); // OR Token
            final Pair<Term, Integer> term = parseTerm(tokens);
            final Optional<Pair<ExpressionStrich, Integer>> expressionStrich = parseExpressionStrich(tokens);
            return Optional.of(Pair.of(
                    ExpressionStrich.builder()
                            .term(term.getLeft())
                            .expressionStrich(expressionStrich.map(Pair::getLeft))
                            .build(),
                    1 + term.getRight() + expressionStrich.map(Pair::getRight).orElse(0)
            ));
        }
    }

    private Pair<Term, Integer> parseTerm(final Queue<Token> tokens) {
        final Pair<Factor, Integer> factor = parseFactor(tokens);
        final Optional<Pair<TermStrich, Integer>> termStrich = parseTermStrich(tokens);
        return Pair.of(Term.builder()
                        .factor(factor.getLeft())
                        .termStrich(termStrich.map(Pair::getLeft))
                        .build(),
                factor.getRight() + termStrich.map(Pair::getRight).orElse(0)
        );
    }

    private Optional<Pair<TermStrich, Integer>> parseTermStrich(final Queue<Token> tokens) {
        if (tokens.isEmpty() || !tokens.peek().getType().equals(Token.Type.AND)) {
            return Optional.empty();
        } else {
            final Token token = tokens.remove(); // AND Token
            final Pair<Factor, Integer> factor = parseFactor(tokens);
            final Optional<Pair<TermStrich, Integer>> termStrich = parseTermStrich(tokens);
            return Optional.of(Pair.of(
                    TermStrich.builder()
                            .term(termStrich.map(Pair::getLeft))
                            .factor(factor.getLeft())
                            .build(),
                    1 + factor.getRight() + termStrich.map(Pair::getRight).orElse(0)
            ));
        }
    }

    private Pair<Factor, Integer> parseFactor(final Queue<Token> tokens) {
        final Token token = tokens.remove();
        if (token.getType().equals(Token.Type.OPENING_PARENTHESIS)) {
            final Pair<Expression, Integer> expression = parseExpression(tokens);
            if (!tokens.remove().getType().equals(Token.Type.CLOSING_PARENTHESIS)) {
                throw new EGovException(ExceptionCode.COMPILER_ERROR, "unable to parse factor. Token: " + token.getType() + " should be closing parentheses");
            }
            return Pair.of(
                    Factor.builder()
                            .filter(Optional.empty())
                            .expression(Optional.of(expression.getLeft()))
                            .build(),
                    expression.getRight() + 2
            );
        } else {
            return Pair.of(
                    Factor.builder()
                            .filter(Optional.of((String) token.getValue()))
                            .expression(Optional.empty())
                            .build(),
                    1
            );
        }
    }

}
