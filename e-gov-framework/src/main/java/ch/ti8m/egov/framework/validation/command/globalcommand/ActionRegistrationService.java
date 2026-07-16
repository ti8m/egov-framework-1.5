package ch.ti8m.egov.framework.validation.command.globalcommand;

import ch.ti8m.egov.framework.validation.command.globalcommand.execution.ZookeeperConnector;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ActionRegistrationService {

    private Map<String, ActionConfiguration> actionConfigurationMap;
    @Autowired
    private ZookeeperConnector zookeeperConnector;

    @PostConstruct
    public void init() {
        actionConfigurationMap = new ConcurrentHashMap<>();
    }

    public void registerActions(final List<ActionConfiguration> actionConfigurations) {
        actionConfigurations.forEach(this::registerAction);
    }

    public void registerAction(final ActionConfiguration actionConfiguration) {
        actionConfigurationMap.put(actionConfiguration.getAction(), actionConfiguration);
        if (log.isInfoEnabled()) {
            log.info("[GLOBAL_ACTION] Registered global action locally: " + actionConfiguration.getAction());
        }
        if (ActionConfiguration.Type.GLOBAL.equals(actionConfiguration.getType())) {
            zookeeperConnector.registerRemoteCommand(actionConfiguration.getAction());
        }
    }

    public ActionConfiguration getActionConfiguration(final String action) {
        return actionConfigurationMap.get(action);
    }

    public Map<String, ActionConfiguration> getAllActionConfigurations() {
        return actionConfigurationMap;
    }

}
