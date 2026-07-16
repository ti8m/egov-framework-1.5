package ch.ti8m.egov.framework.validation.command.globalcommand;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.action.BaseAction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/validation/v1")
@Slf4j
public class RemoteCommandRestController {

    private final GlobalCommandRunner globalCommandRunner;

    @Autowired
    public RemoteCommandRestController(GlobalCommandRunner globalCommandRunner) {
        this.globalCommandRunner = globalCommandRunner;
    }

    @PostMapping("/command/remote/invocation")
    public Object invokeRemoteCommand(
            @RequestBody final RemoteCommandInvocationDto remoteCommandInvocationDto
    ) {
        log.debug("Received RCI for action: {}", remoteCommandInvocationDto.getAction());
        final Command command = globalCommandRunner.getCommand(
                new RemoteAction(remoteCommandInvocationDto.getAggregate(), remoteCommandInvocationDto.getAction()),
                remoteCommandInvocationDto.getPayload(),
                remoteCommandInvocationDto.getUserId(),
                remoteCommandInvocationDto.getParameters()
        );
        DataHolder.setUserId(remoteCommandInvocationDto.getUserId());
        return globalCommandRunner.run(command);
    }

    @AllArgsConstructor
    private static class RemoteAction implements BaseAction {

        private final String aggregateName;
        private final String action;

        @Override
        public String getAggregateName() {
            return aggregateName;
        }

        @Override
        public String getAction() {
            return action;
        }
    }

}