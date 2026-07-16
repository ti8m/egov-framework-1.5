package ch.ti8m.egov.demo.shared;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("h2")
public class H2ServerConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TcpServer() throws Exception {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9093");
    }

}
