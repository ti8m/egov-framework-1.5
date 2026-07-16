package ch.ti8m.egov.framework.validation.command.globalcommand.execution;

import ch.ti8m.egov.framework.deployconfig.ServiceDiscoveryConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ZookeeperConfig {

    private static final int ZOOKEEPER_BASE_SLEEP_TIME_MS = 1000;
    private static final int ZOOKEEPER_MAX_RETRIES = 10;

    private final ServiceDiscoveryConfigurationService serviceDiscoveryConfigurationService;
    private final ZookeeperSaslConfig zookeeperSaslConfig;

    @Bean
    @ConditionalOnProperty(value = "egov.service.discovery.activated", havingValue = "true")
    public CuratorFramework curatorClient() {
        applyLoginConfig();

        final CuratorFramework client = CuratorFrameworkFactory
                .newClient(
                        serviceDiscoveryConfigurationService.getZookeeperHost() + ":" + serviceDiscoveryConfigurationService.getZookeeperPort(),
                        new ExponentialBackoffRetry(ZOOKEEPER_BASE_SLEEP_TIME_MS, ZOOKEEPER_MAX_RETRIES)
                );
        client.start();
        return client;
    }

    @Bean
    @ConditionalOnProperty(value = "egov.service.discovery.activated", havingValue = "false", matchIfMissing = true)
    public CuratorFramework curatorClientFallback() {
        return (CuratorFramework) Proxy.newProxyInstance(
                CuratorFramework.class.getClassLoader(),
                new Class[]{CuratorFramework.class},
                (proxy, method, args) -> {
                    log.warn("Using fallback curator client.");
                    return switch (method.getName()) {
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "toString" -> proxy.toString();
                        case "close" -> null;
                        default -> throw new NotImplementedException();
                    };
                }
        );
    }

    private void applyLoginConfig() {
        if (serviceDiscoveryConfigurationService.isZookeeperAuthenticationActivated()) {
            javax.security.auth.login.Configuration.setConfiguration(zookeeperSaslConfig);
        }
    }

}
