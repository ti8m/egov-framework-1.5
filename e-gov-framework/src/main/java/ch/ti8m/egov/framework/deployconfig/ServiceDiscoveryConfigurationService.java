package ch.ti8m.egov.framework.deployconfig;

import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldNameConstants
public class ServiceDiscoveryConfigurationService {
    @Value("${egov.service.discovery.zookeeper.port:2181}")
    private int zookeeperPort;
    @Value("${egov.service.discovery.zookeeper.host:zookeeper}")
    private String zookeeperHost;
    @Value("${egov.service.discovery.zookeeper.authentication.activated:false}")
    private boolean zookeeperAuthenticationActivated;
    @Value("${egov.service.discovery.zookeeper.authentication.username:zookeeper}")
    private String zookeeperAuthenticationUser;
    @Value("${egov.service.discovery.zookeeper.authentication.password:zookeeper}")
    private String zookeeperAuthenticationPassword;
    @Value("${egov.service.discovery.callback.url:egov:8080/egov/validation/v1/command/remote/invocation}")
    private String remoteCommandCallbackURL;
    @Value("${egov.service.discovery.activated:false}")
    private boolean activated;
}
