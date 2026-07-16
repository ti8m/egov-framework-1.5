package ch.ti8m.egov.demo.domain.gescheaft.service;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CreateGescheaftExecutor implements Executor {

    @Override
    public Long execute(final Command command) {
        return new Random().nextLong();
    }

}
