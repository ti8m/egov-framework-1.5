package ch.ti8m.egov.demo.domain.gescheaft.controller;

import ch.ti8m.egov.demo.domain.gescheaft.dto.CreateGescheaftDto;
import ch.ti8m.egov.demo.domain.gescheaft.validation.GescheaftAction;
import ch.ti8m.egov.demo.domain.gescheaft.validation.GescheaftApplicationServiceProxy;
import ch.ti8m.egov.framework.validation.command.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gescheaft")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GescheaftController {

    private final GescheaftCommandFactory gescheaftCommandFactory;
    private final GescheaftApplicationServiceProxy gescheaftApplicationServiceProxy;

    @PostMapping("/geschaefte")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createGescheaft(
            @RequestBody final CreateGescheaftDto createGescheaftDto
    ) {
        final Command command = gescheaftCommandFactory.getCommand(GescheaftAction.CREATE_GESCHEAFT, createGescheaftDto);
        return gescheaftApplicationServiceProxy.handleCommand(command);
    }

}
