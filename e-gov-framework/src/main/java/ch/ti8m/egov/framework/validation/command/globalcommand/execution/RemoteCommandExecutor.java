package ch.ti8m.egov.framework.validation.command.globalcommand.execution;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.rest.config.RCIHttpHeaderProvider;
import ch.ti8m.egov.framework.rest.config.RestTemplateConfig;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.NonTxExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Slf4j
public class RemoteCommandExecutor implements NonTxExecutor {

    public static final String REMOTE_COMMAND_AGGREGATE = "remote";

    private final RCIHttpHeaderProvider httpHeaderProvider;
    private final ZookeeperConnector zookeeperConnector;
    private final RestTemplate restTemplate;

    public RemoteCommandExecutor(
            final RCIHttpHeaderProvider httpHeaderProvider,
            final ZookeeperConnector zookeeperConnector,
            @Qualifier(RestTemplateConfig.RCI_REST_TEMPLATE_QUALIFIER) final RestTemplate restTemplate
    ) {
        this.httpHeaderProvider = httpHeaderProvider;
        this.zookeeperConnector = zookeeperConnector;
        this.restTemplate = restTemplate;
    }

    @Override
    public Object execute(final Command command) {
        final RemoteCommandInvocationDto remoteCommandInvocationDto = new RemoteCommandInvocationDto(
                command.getAction(),
                RemoteCommandExecutor.REMOTE_COMMAND_AGGREGATE,
                command.getCommandValue(),
                DataHolder.getUserId(),
                command.getParameters()
        );

        final String remoteUrl = zookeeperConnector.getRemoteCommandInvocationUrl(command.getAction());
        RemoteCommandExecutor.log.debug("[REMOTE COMMAND] Invoking remote command: {} at {}", command.getAction(), remoteUrl);

        final ResponseEntity<Object> result = restTemplate.exchange(
                remoteUrl,
                HttpMethod.POST,
                new HttpEntity<>(remoteCommandInvocationDto, httpHeaderProvider.provideHttpHeaders()),
                Object.class
        );
        if (result.getStatusCode().is4xxClientError() || result.getStatusCode().is5xxServerError()) {
            throw new EGovException(
                    ExceptionCode.REMOTE_COMMAND_INVOCATION_EXCEPTION,
                    "Error invoking remote command.",
                    Map.of(
                            "action", remoteCommandInvocationDto.getAction(),
                            "userId", remoteCommandInvocationDto.getUserId(),
                            "response", result.getBody() == null
                                    ? ""
                                    : result.getBody().toString()
                    )
            );
        }
        return result.getBody();
    }

}
