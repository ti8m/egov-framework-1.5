package ch.ti8m.egov.framework.persistence.base;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GlobalRepositoryConfigurationService {

    private final List<String> deactivatedRepositoryPermissions = new ArrayList<>();

    public boolean isDeactivated(final Class<? extends BaseRepositoryImpl> repositoryClass) {
        return deactivatedRepositoryPermissions.contains(repositoryClass.getName());
    }

    public void deactivateRepositoryPermissions(final Class<? extends BaseRepositoryImpl> repositoryClass) {
        deactivatedRepositoryPermissions.add(repositoryClass.getName());
    }

    public void activateRepositoryPermissions(final Class<? extends BaseRepositoryImpl> repositoryClass) {
        deactivatedRepositoryPermissions.remove(repositoryClass.getName());
    }

}
