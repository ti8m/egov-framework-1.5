package ch.ti8m.egov.framework.validation.command.globalcommand.execution;

import ch.ti8m.egov.framework.deployconfig.ServiceDiscoveryConfigurationService;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZookeeperConnector {

    public static final String ZOOKEEPER_SERVICE_DISCOVERY_IS_NOT_ACTIVATED = "[ZOOKEEPER] Service discovery is not activated";
    private static final String WATCH_PATH = "/";
    private final ServiceDiscoveryConfigurationService serviceDiscoveryConfigurationService;
    private final CuratorFramework client;
    private final ZookeeperConfigCache zookeeperConfigCache;

    @PostConstruct
    public void init() {
        if (serviceDiscoveryConfigurationService.isActivated()) {
            final CuratorCache curatorCache = CuratorCache.build(client, WATCH_PATH);
            final CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder()
                    .forCreates(node -> zookeeperConfigCache.setConfig(node.getPath(), new String(node.getData())))
                    .forChanges((oldNode, node) -> zookeeperConfigCache.setConfig(node.getPath(), new String(node.getData())))
                    .forDeletes(node -> zookeeperConfigCache.removeConfig(node.getPath()))
                    .build();
            curatorCache.listenable().addListener(curatorCacheListener);
            curatorCache.start();
        }
    }

    public String getRemoteCommandInvocationUrl(final String action) {
        if (!serviceDiscoveryConfigurationService.isActivated()) {
            log.warn(ZOOKEEPER_SERVICE_DISCOVERY_IS_NOT_ACTIVATED);
            return null;
        }
        return zookeeperConfigCache.getUrl(action)
                .orElseThrow(() -> new EGovException(ExceptionCode.ZOOKEEPER_ERROR, "No configuration for action " + action + " found."));
    }

    public void registerRemoteCommand(final String action) {
        if (!serviceDiscoveryConfigurationService.isActivated()) {
            log.info(ZOOKEEPER_SERVICE_DISCOVERY_IS_NOT_ACTIVATED);
            return;
        }
        try {
            client.create()
                    .orSetData()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(WATCH_PATH + action, serviceDiscoveryConfigurationService.getRemoteCommandCallbackURL().getBytes(StandardCharsets.UTF_8));
            log.info("[GLOBAL_ACTION] Registered global action at Zookeeper: {}", action);
        } catch (final Exception e) {
            log.error("[ZOOKEEPER] Error registering remote command: {}", action);
            throw new EGovException(ExceptionCode.ZOOKEEPER_ERROR, e.getMessage(), e);
        }
    }

    public void unregisterRemoteCommand(final String action) {
        if (!serviceDiscoveryConfigurationService.isActivated()) {
            log.info(ZOOKEEPER_SERVICE_DISCOVERY_IS_NOT_ACTIVATED);
            return;
        }
        try {
            client.delete()
                    .deletingChildrenIfNeeded()
                    .forPath(WATCH_PATH + action);
        } catch (final Exception e) {
            log.warn("[ZOOKEEPER] Error removing remote command: {}", action);
        }
    }

}
