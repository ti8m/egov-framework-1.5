package ch.ti8m.egov.framework.persistence.query.filter.parsing.tokenizer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Queue;

@ExtendWith(MockitoExtension.class)
class TokenTest {

    @InjectMocks
    private Tokenizer tokenizer;

    @Test
    void correctFilterToken() {
        final Queue<Token> tokens = tokenizer.tokenize("  geschaeftInfoId=='55244'   ");
        final Token token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.FILTER);
        Assertions.assertThat(token.getValue()).isEqualTo("geschaeftInfoId=='55244'");
    }

    @Test
    void correctFilterTokenForNumber() {
        final Queue<Token> tokens = tokenizer.tokenize("  geschaeftInfoId==55244   ");
        final Token token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.FILTER);
        Assertions.assertThat(token.getValue()).isEqualTo("geschaeftInfoId==55244");
    }

    @Test
    void correctFilterTokenForBoolean() {
        final Queue<Token> tokens = tokenizer.tokenize("  standard==true   ");
        final Token token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.FILTER);
        Assertions.assertThat(token.getValue()).isEqualTo("standard==true");
    }

    @Test
    void correctOrToken() {
        final Queue<Token> tokens = tokenizer.tokenize("  oR   ");
        final Token token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OR);
    }

    @Test
    void correctAndToken() {
        final Queue<Token> tokens = tokenizer.tokenize("  aNd   ");
        final Token token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.AND);
    }

    @Test
    void correctClosingParenthesisToken() {
        final Queue<Token> tokens = tokenizer.tokenize("  )   ");
        final Token token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.CLOSING_PARENTHESIS);
    }

    @Test
    void correctOpeningParenthesisToken() {
        final Queue<Token> tokens = tokenizer.tokenize("  (   ");
        final Token token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OPENING_PARENTHESIS);
    }

    @Test
    void parseLikeExpression() {
        final Queue<Token> tokens = tokenizer.tokenize("(titel_de LIKE '%Verkauf%')");
        Token token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OPENING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.FILTER);
        Assertions.assertThat(token.getValue()).isEqualTo("titel_de LIKE '%Verkauf%'");
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.CLOSING_PARENTHESIS);
    }

    @Test
    void parseNotLikeExpression() {
        final Queue<Token> tokens = tokenizer.tokenize("(titel_de NOT LIKE '%Verkauf%')");
        Token token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OPENING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.FILTER);
        Assertions.assertThat(token.getValue()).isEqualTo("titel_de NOT LIKE '%Verkauf%'");
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.CLOSING_PARENTHESIS);
    }

    @Test
    void parseLikeExpressionComplex() {
        final Queue<Token> tokens = tokenizer.tokenize("((titel_de LIKE '%Verkauf%') OR (titel_it LIKE '%Migliore%'))");
        Token token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OPENING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OPENING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.FILTER);
        Assertions.assertThat(token.getValue()).isEqualTo("titel_de LIKE '%Verkauf%'");
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.CLOSING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OR);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OPENING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.FILTER);
        Assertions.assertThat(token.getValue()).isEqualTo("titel_it LIKE '%Migliore%'");
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.CLOSING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.CLOSING_PARENTHESIS);
    }

    @Test
    void parseInExpression() {
        final Queue<Token> tokens = tokenizer.tokenize("(entwurfId IN ('1', '2','30'))");
        Token token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OPENING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.FILTER);
        Assertions.assertThat(token.getValue()).isEqualTo("entwurfId IN ('1', '2','30')");
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.CLOSING_PARENTHESIS);
    }

    @Test
    void realWorldFilter() {
        final Queue<Token> tokens = tokenizer.tokenize("(GeschaeftTypId=='10') " +
                "AND ((StandKategorieCode!='ABGESCHLOSSEN') " +
                "AND (StandKategorieCode!='UEBERWIESEN') " +
                "AND (OrganId=='106858')) " +
                "AND ((entw_0_titel_de LIKE '%kein%') " +
                "OR (entw_0_titel_fr LIKE '%kein%') " +
                "OR (entw_0_titel_it LIKE '%kein%') " +
                "OR (titel_de LIKE '%kein%') " +
                "OR (titel_fr LIKE '%kein%') " +
                "OR (titel_it LIKE '%kein%') " +
                "OR (GeschaeftNummer LIKE '%kein%'))");

        Assertions.assertThat(tokens.size()).isEqualTo(47);
    }

    @Test
    void complexFilter() {
        final Queue<Token> tokens = tokenizer.tokenize("((geschaeftInfoId=='55244') OR (tags.tag=='NEU')) AND (geschaeftInfoId=='55243')");
        Token token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OPENING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OPENING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.FILTER);
        Assertions.assertThat(token.getValue()).isEqualTo("geschaeftInfoId=='55244'");
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.CLOSING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OR);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OPENING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.FILTER);
        Assertions.assertThat(token.getValue()).isEqualTo("tags.tag=='NEU'");
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.CLOSING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.CLOSING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.AND);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.OPENING_PARENTHESIS);
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.FILTER);
        Assertions.assertThat(token.getValue()).isEqualTo("geschaeftInfoId=='55243'");
        token = tokens.remove();
        Assertions.assertThat(token.getType()).isEqualTo(Token.Type.CLOSING_PARENTHESIS);
        Assertions.assertThat(tokens).isEmpty();
    }

}