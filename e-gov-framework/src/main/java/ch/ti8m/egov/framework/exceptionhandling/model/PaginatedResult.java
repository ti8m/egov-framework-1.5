package ch.ti8m.egov.framework.exceptionhandling.model;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class PaginatedResult<T> {

    private List<T> results;
    private Integer count;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer next;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer prev;

    public PaginatedResult(List<T> results, Integer count) {
        this.results = results;
        this.count = count;
        if (DataHolder.getPage() != null && DataHolder.getSize() != null) {
            // page-based pagination
            if (DataHolder.getPage() > 0) {
                this.prev = DataHolder.getPage() - 1;
            }
            if (DataHolder.getOffset() + DataHolder.getLimit() < count) {
                this.next = DataHolder.getPage() + 1;
            }
        }
    }
}
