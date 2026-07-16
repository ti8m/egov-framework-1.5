package ch.ti8m.egov.framework.persistence.query;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.iam.api.model.PermissionOperation;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Builder(builderClassName = "Builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryConfig<T extends ModifiableEntity> {

    private final EntityMetadata<T> entityMetadata;

    private final PermissionOperation operation;

    private final String filter;
    private final boolean applyFilter;

    private final String sorting;
    private final boolean applySorting;

    private final boolean applyPagination;

    private final boolean skipPermissions;
    private final boolean includeCountQuery;
    private final boolean includeArchived;

    private final ParametrizedQuery inputQuery;

    private final Collection<Long> inputIds;
    private final List<Pair<String, Object>> inputParams;

    public static <T extends ModifiableEntity> Builder<T> builder(EntityMetadata<T> entityMetadata) {
        return new Builder<T>()
                .entityMetadata(entityMetadata);
    }

    public static class Builder<T extends ModifiableEntity> {
        public Builder<T> inputParamsWithColumnValuePairs(Object... params) {
            if (params == null) {
                return this;
            }
            if (params.length % 2 != 0) {
                throw new EGovException(ExceptionCode.FILTER_PARAMS_INVALID, "Filter Params must be a pair of column and value, or empty or null");
            }
            var inputParamsTemp = new ArrayList<Pair<String, Object>>();
            for (int i = 0; i < params.length; i += 2) {
                Object column = params[i];
                Object value = params[i + 1];
                if (column == null) {
                    throw new EGovException(ExceptionCode.FILTER_PARAMS_INVALID, "Filter Params: column was null!");
                } else if (!(column instanceof String)) {
                    throw new EGovException(ExceptionCode.FILTER_PARAMS_INVALID, "Filter Params: column must be a string value!");
                }
                inputParamsTemp.add(Pair.of((String) column, value));
            }
            this.inputParams(inputParamsTemp);
            return this;
        }

    }

}
