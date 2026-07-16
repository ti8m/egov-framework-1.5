package ch.ti8m.egov.framework.rest.config;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.globalcommand.execution.RemoteCommandExecutor;
import ch.ti8m.egov.framework.validation.command.globalcommand.execution.ZookeeperConnector;
import ch.ti8m.egov.testbase.TestApplicationContext;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class RestTemplateConfigIT extends TestApplicationContext {

    @Autowired
    private RemoteCommandExecutor executor;

    @Autowired
    private RCIHttpHeaderProvider httpHeaderProvider;

    @MockBean
    private ZookeeperConnector zookeeperConnector;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() {
        Mockito.when(zookeeperConnector.getRemoteCommandInvocationUrl(Mockito.any())).thenReturn(mockWebServer.url("/test").toString());
    }

    @BeforeAll
    void setupServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    void stopServer() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void correctParsingOfStrings() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("\"test\"").addHeader("Content-Type", "application/json"));

        Assertions.assertWith(executor.execute(new Command()), result -> {
            Assertions.assertThat(result).isInstanceOf(String.class);
            Assertions.assertThat(result).isEqualTo("test");
        });
    }

    @Test
    void correctParsingOfNulls() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("").addHeader("Content-Type", "application/json"));

        Assertions.assertWith(executor.execute(new Command()), result -> {
            Assertions.assertThat(result).isEqualTo(null);
        });
    }

    @Test
    void correctParsingOfLongs() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("123").addHeader("Content-Type", "application/json"));

        Assertions.assertWith(executor.execute(new Command()), result -> {
            Assertions.assertThat(result).isInstanceOf(Long.class);
            Assertions.assertThat(result).isEqualTo(123L);
        });
    }

    @Test
    void correctParsingOfDoubles() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("1.23").addHeader("Content-Type", "application/json"));

        Assertions.assertWith(executor.execute(new Command()), result -> {
            Assertions.assertThat(result).isInstanceOf(Double.class);
            Assertions.assertThat(result).isEqualTo(1.23D);
        });
    }

    @Test
    void correctParsingOfObjects() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}").addHeader("Content-Type", "application/json"));

        Assertions.assertWith(executor.execute(new Command()), result -> {
            Assertions.assertThat(result).isInstanceOf(Map.class);
            Assertions.assertThat(result).isEqualTo(Collections.emptyMap());
        });
    }

    @Test
    void correctParsingOfLists() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]").addHeader("Content-Type", "application/json"));

        Assertions.assertWith(executor.execute(new Command()), result -> {
            Assertions.assertThat(result).isInstanceOf(List.class);
            Assertions.assertThat(result).isEqualTo(Collections.emptyList());
        });
    }

}