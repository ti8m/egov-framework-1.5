package ch.ti8m.egov.demo.domain.gescheaft.validation;

import ch.ti8m.egov.demo.domain.gescheaft.persistence.Gescheaft;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.handler.CommandHandlerBase;
import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;
import org.springframework.stereotype.Component;

@Component
public class GescheaftApplicationService extends CommandHandlerBase<Gescheaft> implements GescheaftApplicationServiceValidation {

// uncomment if you need aggregate access for validations
// private final GescheaftRepository gescheaftRepository;

// @Autowired
// public GescheaftApplicationService(
//      final GescheaftRepository gescheaftRepository
//  ) {
//      this.gescheaftRepository = gescheaftRepository;
//  }

    @Override
    protected void loadAggregate(final Command command) {
        // If a validation needs the aggregate, uncomment the following lines. Otherwise, leave it commented or remove it to enhance performance.
        // super.setAggregate(gescheaftRepository.findById(command.getAggregateId())
        //     .orElseThrow(() -> new IllegalArgumentException("Gescheaft not found")));
    }

    @Override
    protected ValidationMethodMapper provideValidationMethodMapper() {
        return new GescheaftValidationMethodMapperImpl(this);
    }

}
