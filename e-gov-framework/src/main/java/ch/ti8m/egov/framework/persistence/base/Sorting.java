package ch.ti8m.egov.framework.persistence.base;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class Sorting {

    private Sorting() {
    }

    public static SortingBuilder builder() {
        return new SortingBuilder();
    }

    public static class SortingBuilder {

        private final Map<String, String> sortingMap = new LinkedHashMap<>();
        private final SortingBuilder sortingBuilder = this;
        private final SortingOrderBuilder sortingOrderBuilder = new SortingOrderBuilder();
        private String tempField;

        public SortingOrderBuilder field(final String field) {
            tempField = field;
            return sortingOrderBuilder;
        }

        public class SortingOrderBuilder {

            private final SortingConcat sortingConcat = new SortingConcat();

            public SortingConcat ascending() {
                sortingMap.put(tempField, "asc");
                return sortingConcat;
            }

            public SortingConcat descending() {
                sortingMap.put(tempField, "desc");
                return sortingConcat;
            }

            public class SortingConcat {

                public String get() {
                    return sortingMap.entrySet()
                            .stream()
                            .map(sortEntry -> sortEntry.getKey() + ":" + sortEntry.getValue())
                            .collect(Collectors.joining(","));
                }

                public SortingBuilder and() {
                    return sortingBuilder;
                }

            }

        }

    }

}
