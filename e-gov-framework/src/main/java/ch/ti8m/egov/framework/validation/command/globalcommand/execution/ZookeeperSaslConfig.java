package ch.ti8m.egov.framework.validation.command.globalcommand.execution;

import ch.ti8m.egov.framework.deployconfig.ServiceDiscoveryConfigurationService;
import org.apache.zookeeper.server.auth.DigestLoginModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class ZookeeperSaslConfig extends javax.security.auth.login.Configuration {

    private final Optional<AppConfigurationEntry> authenticationConfiguration;

    @Autowired
    public ZookeeperSaslConfig(ServiceDiscoveryConfigurationService serviceDiscoveryConfigurationService) {
        if (serviceDiscoveryConfigurationService.isZookeeperAuthenticationActivated()) {
            Map<String, String> authOptions = new HashMap<>();
            authOptions.put("username", serviceDiscoveryConfigurationService.getZookeeperAuthenticationUser());
            authOptions.put("password", serviceDiscoveryConfigurationService.getZookeeperAuthenticationPassword());

            authenticationConfiguration = Optional.of(new AppConfigurationEntry(
                    DigestLoginModule.class.getName(),
                    AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                    authOptions
            ));
        } else {
            authenticationConfiguration = Optional.empty();
        }
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        return authenticationConfiguration.map(entry -> {
                    AppConfigurationEntry[] array = new AppConfigurationEntry[1];
                    array[0] = entry;
                    return array;
                })
                .orElse(new AppConfigurationEntry[0]);
    }

}
