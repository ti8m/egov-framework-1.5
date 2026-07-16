package ch.ti8m.egov.framework.exceptionhandling.context;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RepositoryInstanceContext {
    private final List<Long> entityIds = new ArrayList<>();
    private boolean skipPermissions = false;
    private boolean skipCountQuery = false;
}
