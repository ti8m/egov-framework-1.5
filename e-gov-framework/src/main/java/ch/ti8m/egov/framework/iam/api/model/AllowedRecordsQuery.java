package ch.ti8m.egov.framework.iam.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AllowedRecordsQuery {

    private String query;
    private List<Object> values;

}
