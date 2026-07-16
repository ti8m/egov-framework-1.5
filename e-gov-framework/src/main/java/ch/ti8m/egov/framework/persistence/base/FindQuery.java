package ch.ti8m.egov.framework.persistence.base;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FindQuery {

    private String query;
    private List<Object> parameters;

    private String countQuery;
    private boolean includeCount;

    private String rawQuery;

}
