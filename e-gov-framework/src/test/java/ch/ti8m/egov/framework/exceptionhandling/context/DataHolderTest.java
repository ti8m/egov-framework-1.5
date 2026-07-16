package ch.ti8m.egov.framework.exceptionhandling.context;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataHolderTest {

    @Test
    void pageSizeInput_offsetLimitCorrect_firstPage() {
        final int page = 0;
        final int size = 5;
        final int expectedOffset = 0;
        final int expectedLimit = 5;

        DataHolder.setPage(page);
        DataHolder.setSize(size);

        Assertions.assertThat(DataHolder.getOffset()).isEqualTo(expectedOffset);
        Assertions.assertThat(DataHolder.getLimit()).isEqualTo(expectedLimit);
    }

    @Test
    void pageSizeInput_offsetLimitCorrect_nPage() {
        final int page = 3;
        final int size = 5;
        final int expectedOffset = 15;
        final int expectedLimit = 5;

        DataHolder.setPage(page);
        DataHolder.setSize(size);

        Assertions.assertThat(DataHolder.getOffset()).isEqualTo(expectedOffset);
        Assertions.assertThat(DataHolder.getLimit()).isEqualTo(expectedLimit);
    }

}
