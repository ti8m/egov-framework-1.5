package ch.ti8m.egov.framework.validation.command.globalcommand.execution;

import ch.ti8m.egov.testbase.TestApplicationContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class ZookeeperTestContainer extends TestApplicationContext {
    private static final String ZOOKEEPER_IMAGE = "zookeeper:3.9.3";
    private static final GenericContainer<?> zookeeperContainer =
            new GenericContainer<>(DockerImageName.parse(ZOOKEEPER_IMAGE))
                    .withExposedPorts(2181);

    @BeforeAll
    static void startContainer() {
        zookeeperContainer.start();
        System.setProperty("egov.service.discovery.zookeeper.port", String.valueOf(getZookeeperPort()));
        System.setProperty("egov.service.discovery.zookeeper.host", zookeeperContainer.getHost());
    }

    @AfterAll
    static void stopContainer() {
        zookeeperContainer.stop();
    }

    protected static Integer getZookeeperPort() {
        return zookeeperContainer.getMappedPort(2181);
    }

    @Test
    void testZookeeperContainerIsRunning() {
        Assertions.assertThat(zookeeperContainer.isRunning()).isTrue();
        System.out.println("Zookeeper running on port: " + getZookeeperPort() + " and host: " + zookeeperContainer.getHost());
    }

}
