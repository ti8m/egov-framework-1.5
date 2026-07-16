package ch.ti8m.egov.framework.validation.command.globalcommand.execution;

import ch.ti8m.egov.framework.deployconfig.ServiceDiscoveryConfigurationService;
import org.apache.zookeeper.server.auth.DigestLoginModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.security.auth.login.AppConfigurationEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ZookeeperSaslConfigTest {

    private ServiceDiscoveryConfigurationService serviceDiscoveryConfigurationService;
    private ZookeeperSaslConfig zookeeperSaslConfig;

    @BeforeEach
    void setUp() {
        serviceDiscoveryConfigurationService = mock(ServiceDiscoveryConfigurationService.class);
    }

    @Test
    void getAppConfigurationEntry_whenAuthenticationActivated_returnsConfiguration() {
        when(serviceDiscoveryConfigurationService.isZookeeperAuthenticationActivated()).thenReturn(true);
        when(serviceDiscoveryConfigurationService.getZookeeperAuthenticationUser()).thenReturn("user");
        when(serviceDiscoveryConfigurationService.getZookeeperAuthenticationPassword()).thenReturn("pwd");

        zookeeperSaslConfig = new ZookeeperSaslConfig(serviceDiscoveryConfigurationService);

        AppConfigurationEntry[] entries = zookeeperSaslConfig.getAppConfigurationEntry("anyName");

        assertThat(entries).hasSize(1);
        assertThat(entries[0].getLoginModuleName()).isEqualTo(DigestLoginModule.class.getName());
        assertThat(entries[0].getControlFlag()).isEqualTo(AppConfigurationEntry.LoginModuleControlFlag.REQUIRED);
        assertThat(entries[0].getOptions())
                .extracting("username", "password")
                .containsExactly("user", "pwd");
    }

    @Test
    void getAppConfigurationEntry_whenAuthenticationNotActivated_returnsEmptyArray() {
        when(serviceDiscoveryConfigurationService.isZookeeperAuthenticationActivated()).thenReturn(false);

        zookeeperSaslConfig = new ZookeeperSaslConfig(serviceDiscoveryConfigurationService);

        AppConfigurationEntry[] entries = zookeeperSaslConfig.getAppConfigurationEntry("anyName");

        assertThat(entries)
                .isNotNull()
                .isEmpty();
    }
}
