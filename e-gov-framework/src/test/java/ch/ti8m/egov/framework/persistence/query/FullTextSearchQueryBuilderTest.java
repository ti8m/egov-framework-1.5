package ch.ti8m.egov.framework.persistence.query;

import ch.ti8m.egov.framework.deployconfig.DatabaseConfigurationService;
import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FullTextSearchQueryBuilderTest {

    static final String TABLE_NAME = "test";
    static final String ID_COLUMN_NAME = "id";
    static final String CURRENT_DATE_STRING = "_my_current_date_";

    @Mock
    DatabaseConfigurationService databaseConfigurationService;
    @InjectMocks
    FullTextSearchQueryBuilder queryBuilder;

    @Test
    void resolveCurrentDatePlaceholder() {
        Mockito.when(databaseConfigurationService.getCurrentDateString()).thenReturn(FullTextSearchQueryBuilderTest.CURRENT_DATE_STRING);
        DataHolder.initialize("", "", "", 0, 0, "");
        DataHolder.getFullTextSearchConfig().setFieldNameTimeRelevance("fieldName");
        Assertions.assertWith(queryBuilder.getWeightedSearchJoin(
                FullTextSearchQueryBuilderTest.TABLE_NAME,
                FullTextSearchQueryBuilderTest.ID_COLUMN_NAME
        ), result -> {
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getLeft()).isNotNull();
            Assertions.assertThat(result.getLeft()).contains(FullTextSearchQueryBuilderTest.CURRENT_DATE_STRING);
        });
    }

}