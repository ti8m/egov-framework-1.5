package ch.ti8m.egov.framework.validation.command.handler;

import ch.ti8m.egov.framework.iam.api.java.RoleService;
import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.CommandRepository;
import ch.ti8m.egov.framework.validation.command.executor.BaseExecutor;
import ch.ti8m.egov.framework.validation.command.subscription.SubscriptionService;
import ch.ti8m.egov.framework.validation.engine.ValidationEngine;
import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;
import ch.ti8m.egov.framework.validation.engine.ValidationTriple;
import lombok.Data;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class CommandHandlerBaseTest {

    @Mock
    private ValidationEngine validationEngine;
    @Mock
    private CommandRepository commandRepository;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private RoleService roleService;
    @Mock
    private PrimaryRepositoryResolverComponent primaryRepositoryResolverComponent;
    @Mock
    private ValidationMethodMapper validationMethodMapper;
    @InjectMocks
    private CommandHandlerBaseImpl<?> commandHandlerBase;

    // other mocks
    @Mock
    private BaseExecutor executor;

    private Command command;
    private ValidationTriple validationTriple;
    private TestDto objectTestDto;
    private List<TestDto> listTestDto;
    private Map<String, Object> mapValidationResult;
    private List<Map<String, Object>> listValidationResult;

    @BeforeEach
    public void setUp() {
        command = new Command();
        command.setExecutor(executor);
        command.setParameters(Parameters.builder().build());
        command.setExecutionPlan(Command.ExecutionPlan.VALIDATE_BEFORE_EXECUTION);

        validationTriple = ValidationTriple.builder()
                .validationResult(Collections.emptyMap())
                .state(ValidationTriple.ValidationState.VALID)
                .build();
        Mockito.when(validationEngine.validate(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
        )).thenReturn(validationTriple);

        objectTestDto = TestDto.INSTANCE();
        listTestDto = List.of(TestDto.INSTANCE());
        mapValidationResult = Map.of("validatedField", "validatedValue");
        listValidationResult = List.of(Map.of("validatedField", "validatedValue"));
    }

    @Test
    void validate_nonEmptyDtoListAndValidateAsMap_returnsListOfMaps() {
        command.setCommandValue(listTestDto);
        validationTriple.setResponseObject(listValidationResult);

        command.validateValueAsMap();
        commandHandlerBase.handleCommand(command);

        Assertions.assertThat(command.getCommandValue()).isEqualTo(listValidationResult);
    }

    @Test
    void validate_nonEmptyDtoListAndValidateAsDto_returnsUnchangedList() {
        command.setCommandValue(listTestDto);
        validationTriple.setResponseObject(listValidationResult);

        command.validateValueAsDto();
        commandHandlerBase.handleCommand(command);

        Assertions.assertThat(command.getCommandValue()).isEqualTo(listTestDto);
    }

    @Test
    void validate_nonEmptyDtoListAndValidateAsDefault_returnsUnchangedList() {
        command.setCommandValue(listTestDto);
        validationTriple.setResponseObject(listValidationResult);

        command.setValidateValueAsMap(null);
        commandHandlerBase.handleCommand(command);

        Assertions.assertThat(command.getCommandValue()).isEqualTo(listTestDto);
    }

    @Test
    void validate_dtoObjectAndValidateAsMap_returnsMap() {
        command.setCommandValue(objectTestDto);
        validationTriple.setResponseObject(mapValidationResult);

        command.validateValueAsMap();
        commandHandlerBase.handleCommand(command);

        Assertions.assertThat(command.getCommandValue()).isEqualTo(mapValidationResult);
    }

    @Test
    void validate_dtoObjectAndValidateAsDto_returnsUnchangedObject() {
        command.setCommandValue(objectTestDto);
        validationTriple.setResponseObject(mapValidationResult);

        command.validateValueAsDto();
        commandHandlerBase.handleCommand(command);

        Assertions.assertThat(command.getCommandValue()).isEqualTo(objectTestDto);
    }

    @Test
    void validate_dtoObjectAndValidateAsDefault_returnsUnchangedObject() {
        command.setCommandValue(objectTestDto);
        validationTriple.setResponseObject(mapValidationResult);

        command.setValidateValueAsMap(null);
        commandHandlerBase.handleCommand(command);

        Assertions.assertThat(command.getCommandValue()).isEqualTo(objectTestDto);
    }

    private static class CommandHandlerBaseImpl<T> extends CommandHandlerBase<T> {
        @Override
        protected void loadAggregate(final Command command) {

        }

        @Override
        protected ValidationMethodMapper provideValidationMethodMapper() {
            return null;
        }
    }

    @Data
    private static final class TestDto {
        private String value1;
        private String value2;

        public static TestDto INSTANCE() {
            final TestDto testDto = new TestDto();
            testDto.setValue1("some value");
            testDto.setValue2("some other value");
            return testDto;
        }
    }

}