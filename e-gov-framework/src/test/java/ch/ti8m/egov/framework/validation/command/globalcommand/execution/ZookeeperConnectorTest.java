package ch.ti8m.egov.framework.validation.command.globalcommand.execution;

import ch.ti8m.egov.framework.deployconfig.ServiceDiscoveryConfigurationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;


@SpringBootTest
@ActiveProfiles("test-zookeeper")
class ZookeeperConnectorTest extends ZookeeperTestContainer {

    private static final String ACTION = "ACTION_NAME";
    private static final String CALLBACK_URL = "CALLBACK_URL_EGOV";
    private static final String UPDATED_CALLBACK_URL = "CALLBACK_URL_EGOV_updated";

    @Autowired
    private ZookeeperConnector zookeeperConnector;
    @Autowired
    private ServiceDiscoveryConfigurationService serviceDiscoveryConfigurationService;

    @BeforeEach
    void setUp() {
        updateCallbackUrl(CALLBACK_URL);
    }

    @AfterEach
    void tearDown() {
        ZookeeperConfigCache.getActionUrlMap()
                .keySet()
                .forEach(zookeeperConnector::unregisterRemoteCommand);
        waitForNodePropagation();
    }

    @Test
    void newAction_registerRemoteCommand_callbackIsAdded() {
        zookeeperConnector.registerRemoteCommand(ACTION);
        waitForNodePropagation();
        final String callbackUrl = zookeeperConnector.getRemoteCommandInvocationUrl(ACTION);
        Assertions.assertThat(callbackUrl).isEqualTo(CALLBACK_URL);
    }

    @Test
    void actionUpdatedWithDifferentCallBack_registerRemoteCommand_newCallbackIsSet() {
        zookeeperConnector.registerRemoteCommand(ACTION);
        waitForNodePropagation();
        String callbackUrl = zookeeperConnector.getRemoteCommandInvocationUrl(ACTION);
        Assertions.assertThat(callbackUrl).isEqualTo(CALLBACK_URL);
        // change to new callback url
        updateCallbackUrl(UPDATED_CALLBACK_URL);
        zookeeperConnector.registerRemoteCommand(ACTION);
        waitForNodePropagation();
        callbackUrl = zookeeperConnector.getRemoteCommandInvocationUrl(ACTION);
        Assertions.assertThat(callbackUrl).isEqualTo(UPDATED_CALLBACK_URL);
    }

    private void updateCallbackUrl(final String callbackUrl) {
        try {
            final Class<?> targetClass = AopUtils.getTargetClass(serviceDiscoveryConfigurationService);
            final Field callbackUrlField = targetClass.getDeclaredField("remoteCommandCallbackURL");
            callbackUrlField.setAccessible(true);
            callbackUrlField.set(serviceDiscoveryConfigurationService, callbackUrl);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForNodePropagation() {
        try {
            Thread.sleep(5000);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}