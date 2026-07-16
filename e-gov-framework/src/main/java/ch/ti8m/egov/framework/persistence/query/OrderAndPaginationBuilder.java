package ch.ti8m.egov.framework.persistence.query;

import ch.ti8m.egov.framework.deployconfig.DatabaseConfigurationService;
import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.model.join.TableJoins;
import ch.ti8m.egov.framework.persistence.query.filter.parsing.util.JoinUtil;
import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import ch.ti8m.egov.framework.persistence.util.ReflectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAndPaginationBuilder {

    private final NameTranslationComponent nameTranslationComponent;
    private final DatabaseConfigurationService databaseConfigurationService;

    public Triple<String, String, String> getOrderAndPaginationExtension(
            final Class<? extends ModifiableEntity> clazz,
            final String sorting,
            final Boolean includeArchived,
            final boolean isPrimary
    ) {
        final List<Triple<String, String, String>> orders = Arrays.stream(sorting.split(","))
                .map(singleSort -> {
                    final String[] orderQuery = singleSort.split(":");

                    final JoinConfiguration joinConfiguration = new JoinConfiguration(
                            true,
                            false,
                            includeArchived,
                            databaseConfigurationService.getFalseStatement());

                    final Pair<String, TableJoins> pair = JoinUtil.getJoin(
                            clazz,
                            new LinkedList<>(Arrays.asList(orderQuery[0].split("\\."))),
                            new TableJoins(),
                            nameTranslationComponent,
                            false,
                            joinConfiguration);
                    final String joins = JoinUtil.toJoinStatements(pair.getRight().values());

                    return Triple.of(joins,
                            pair.getLeft() + " " + getDirection(orderQuery[1]),
                            joins.isEmpty() ? "" : pair.getLeft()
                    );
                })
                .toList();

        String queryExtension = " ORDER BY " + orders.stream().map(Triple::getMiddle).collect(Collectors.joining(", "));
        if (isPrimary) {
            queryExtension += " OFFSET " + getOffset() + " ROWS"
                    + " FETCH NEXT " + getFetchCount() + " ROWS ONLY";
        }

        return Triple.of(
                orders.get(0).getLeft(),
                queryExtension,
                orders.get(0).getRight()
        );
    }

    private String getOrderBy(
            final Class<? extends ModifiableEntity> clazz,
            final String sorting
    ) {
        try {
            return Stream.concat(
                            DataHolder.getFullTextSearchConfig().isFullTextSearchActive()
                                    ? Stream.of("WeightedRank DESC")
                                    : Stream.empty(),
                            Arrays.stream(sorting.split(","))
                                    .map(order -> {
                                        final String[] orderQuery = order.split(":");
                                        return getColumnForFieldName(clazz, orderQuery[0]) + " " + getDirection(orderQuery[1]);
                                    })
                    )
                    .collect(Collectors.joining(", "));
        } catch (final EGovException e) {
            OrderAndPaginationBuilder.log.warn("Sorting string couldn't be compiled. Class: " + clazz.getName() + " Sorting: " + sorting);
            return getOrderBy(clazz, BaseRepositoryImpl.DEFAULT_ORDER_BY);
        }
    }

    private String getDirection(final String direction) {
        return switch (direction) {
            case "asc" -> "ASC";
            case "desc" -> "DESC";
            default ->
                    throw new EGovException(ExceptionCode.OPERATION_NOT_PERMITTED, "", Map.of("forbidden operation", direction));
        };
    }

    private String getColumnForFieldName(
            final Class<? extends ModifiableEntity> clazz,
            final String fieldName
    ) {
        for (final Field field : ReflectionUtils.getAllEntityFields(clazz)) {
            if (field.getName().equals(fieldName)) {
                return nameTranslationComponent.getTranslatedColumnName(field);
            }
        }
        throw new EGovException(ExceptionCode.FIELD_NOT_FOUND_IN_CLASS, clazz.getSimpleName() + ":" + fieldName, Map.of("unknownField", fieldName, "className", clazz.getName()));
    }

    private int getFetchCount() {
        final Integer providedLimit = DataHolder.getLimit();
        if (providedLimit == null || providedLimit == 0) {
            if (OrderAndPaginationBuilder.log.isDebugEnabled()) {
                OrderAndPaginationBuilder.log.debug("Pagination required but limit not set. Using <<Integer.MAX_VALUE>> as limit.");
            }
            return Integer.MAX_VALUE;
        } else {
            return providedLimit;
        }
    }

    private int getOffset() {
        final Integer providedOffset = DataHolder.getOffset();
        if (providedOffset == null) {
            if (OrderAndPaginationBuilder.log.isDebugEnabled()) {
                OrderAndPaginationBuilder.log.debug("Pagination required but offset not set. Using <<0>> as offset.");
            }
            return 0;
        } else {
            return providedOffset;
        }
    }

}
