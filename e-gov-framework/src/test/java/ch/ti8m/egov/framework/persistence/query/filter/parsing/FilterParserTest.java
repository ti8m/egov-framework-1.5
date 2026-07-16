package ch.ti8m.egov.framework.persistence.query.filter.parsing;

import ch.ti8m.egov.framework.deployconfig.DatabaseConfigurationService;
import ch.ti8m.egov.framework.persistence.query.JoinConfiguration;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.FilterAst;
import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import ch.ti8m.egov.testbase.TestApplicationContext;
import ch.ti8m.egov.testbase.entities.filter.TestFilterEntity1;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class FilterParserTest extends TestApplicationContext {

    JoinConfiguration defaultJoinConfiguration;
    @Autowired
    private NameTranslationComponent nameTranslationComponent;
    @Autowired
    private FilterParser filterParser;
    @Autowired
    private DatabaseConfigurationService databaseConfigurationService;

    @BeforeEach
    void setUp() {
        defaultJoinConfiguration = new JoinConfiguration(
                false,
                true,
                true,
                databaseConfigurationService.getFalseStatement());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("parseWithOnlyLeftExpected_parameters")

    void parseWithOnlyLeftExpected(String name, String input, String expectedQuery) {
        final FilterAst ast = filterParser.parse(input);

        final Pair<String, List<Object>> sql = ast.compileForClass(TestFilterEntity1.class, nameTranslationComponent, defaultJoinConfiguration);

        assertThat(sql.getLeft()).isEqualTo(expectedQuery);
        assertThat(sql.getRight()).isEmpty();
    }

    Stream<Arguments> parseWithOnlyLeftExpected_parameters() {
        return Stream.of(
                Arguments.of("simpleExpressionParsedCorrectlyWithNull",
                        "(field0 IS NULL)",
                        " WHERE ((((((rootTable.field0 IS NULL))))))"),
                Arguments.of("simpleExpressionParsedCorrectlyWithNotNull",
                        "(field0 IS NOT NULL)",
                        " WHERE ((((((rootTable.field0 IS NOT NULL))))))"),
                Arguments.of("simpleExpressionParsedCorrectlyWithInIntegers",
                        "(id IN (1, 2, 30))",
                        " WHERE ((((((rootTable.TEId IN (1, 2, 30)))))))")

        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("parseWithLeftAndRightExpected_parameters")

    void parseWithLeftAndRightExpected(String name, String input, String expectedQuery, List<String> expectedQueryParams, JoinConfiguration joinConfiguration) {
        final FilterAst ast = filterParser.parse(input);
        final Pair<String, List<Object>> sql = ast.compileForClass(TestFilterEntity1.class, nameTranslationComponent, joinConfiguration);

        assertThat(sql.getLeft()).isEqualTo(expectedQuery);
        assertThat(sql.getRight()).isEqualTo(expectedQueryParams);
    }

    Stream<Arguments> parseWithLeftAndRightExpected_parameters() {
        String dbAwareFalseStatement = databaseConfigurationService.getFalseStatement();

        return Stream.of(
                Arguments.of("simpleExpressionParsedCorrectlyWithEquals",
                        "(field0=='2022-02-02')",
                        " WHERE ((((((rootTable.field0 = ?))))))", List.of("2022-02-02"),
                        defaultJoinConfiguration),
                Arguments.of("simpleExpressionParsedCorrectlyWithLessThan",
                        "(field0<'2022-02-02')",
                        " WHERE ((((((rootTable.field0 < ?))))))", List.of("2022-02-02"),
                        defaultJoinConfiguration),
                Arguments.of("simpleExpressionParsedCorrectlyWithLessOrEqualThan",
                        "(field0<='2022-02-02')",
                        " WHERE ((((((rootTable.field0 <= ?))))))", List.of("2022-02-02"),
                        defaultJoinConfiguration),
                Arguments.of("simpleExpressionParsedCorrectlyWithGreaterThan",
                        "(field0>'2022-02-02')",
                        " WHERE ((((((rootTable.field0 > ?))))))", List.of("2022-02-02"),
                        defaultJoinConfiguration),
                Arguments.of("simpleExpressionParsedCorrectlyWithGreaterOrEqualThan",
                        "(field0>='2022-02-02')",
                        " WHERE ((((((rootTable.field0 >= ?))))))", List.of("2022-02-02"),
                        defaultJoinConfiguration),
                Arguments.of("simpleExpressionParsedCorrectlyWithLike",
                        "(field0 LIKE '%MySearch%')",
                        " WHERE ((((((rootTable.field0 LIKE ?))))))", List.of("%MySearch%"),
                        defaultJoinConfiguration),
                Arguments.of("simpleExpressionParsedCorrectlyWithNotLike",
                        "(field0 NOT LIKE '%MySearch%')",
                        " WHERE ((((((rootTable.field0 NOT LIKE ?))))))", List.of("%MySearch%"),
                        defaultJoinConfiguration),
                Arguments.of("simpleExpressionParsedCorrectlyWithNotLikeTooMuchSpaces",
                        "(       id NOT                   LIKE '%MySearch%'        )",
                        " WHERE ((((((rootTable.TEId NOT LIKE ?))))))", List.of("%MySearch%"),
                        defaultJoinConfiguration),
                Arguments.of("simpleExpressionParsedCorrectlyWithIn",
                        "(id IN ('1', '2','30'))",
                        " WHERE ((((((rootTable.TEId IN (?,?,?)))))))", List.of("1", "2", "30"),
                        defaultJoinConfiguration),
                Arguments.of("simpleExpressionParsedCorrectlyWithNotIn",
                        "(id NOT IN ('3','10'))",
                        " WHERE ((((((rootTable.TEId NOT IN (?,?)))))))", List.of("3", "10"),
                        defaultJoinConfiguration),
                Arguments.of("complexJoin",
                        "(testEntityMany.testEntity2.field2=='0') OR (testEntity2s.testEntity3s.field3=='Cool') AND (field0=='2022-02-02')",
                        " LEFT JOIN table4 ON table4.te4id = rootTable.TestEntityManyId LEFT JOIN table2 ON table2.TestEntityId = rootTable.teid LEFT JOIN table3 ON table3.TestEntity2Id = table2.te2id WHERE ((((((table2.Field2 = ?))))) OR (((((table3.Field3 = ?)))) AND ((((rootTable.field0 = ?))))))",
                        List.of("0", "Cool", "2022-02-02"),
                        defaultJoinConfiguration),
                Arguments.of("joinOneToOneJoinColumnDirection",
                        "(testEntityOneTo.field5=='0')",
                        " LEFT JOIN table5 ON table5.te5id = rootTable.TestEntityOneToId WHERE ((((((table5.field5 = ?))))))",
                        List.of("0"),
                        defaultJoinConfiguration),
                Arguments.of("joinOneToOneMappedByDirection",
                        "(testEntityOneFrom.field6=='0')",
                        " LEFT JOIN table6 ON table6.TestEntityId = rootTable.teid WHERE ((((((table6.field6 = ?))))))",
                        List.of("0"),
                        defaultJoinConfiguration),
                Arguments.of("joinOneToOneMappedByDirectionImplicitIdField",
                        "(testEntityOneFromClean.field8=='0')",
                        " LEFT JOIN table8 ON table8.TestEntityId = rootTable.TEId WHERE ((((((table8.field8 = ?))))))",
                        List.of("0"),
                        defaultJoinConfiguration),
                Arguments.of("joinArchivableTable_includeArchived",
                        "(testEntityArchivedModifiable.field7=='0')",
                        " LEFT JOIN table7 ON table7.TestEntityId = rootTable.TEId WHERE ((((((table7.field7 = ?))))))",
                        List.of("0"),
                        defaultJoinConfiguration),
                Arguments.of("joinArchivableTable_excludeArchived",
                        "(testEntityArchivedModifiable.field7=='0')",
                        " LEFT JOIN table7 ON table7.TestEntityId = rootTable.TEId AND table7.archived = " + dbAwareFalseStatement + " WHERE ((((((table7.field7 = ?))))))",
                        List.of("0"),
                        new JoinConfiguration(
                                false,
                                true,
                                false,
                                dbAwareFalseStatement))
        );
    }

}
