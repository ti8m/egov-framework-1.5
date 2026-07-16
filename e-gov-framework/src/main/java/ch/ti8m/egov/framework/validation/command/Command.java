package ch.ti8m.egov.framework.validation.command;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.action.BaseAction;
import ch.ti8m.egov.framework.validation.command.executor.BaseExecutor;
import ch.ti8m.egov.framework.validation.engine.ValidationTriple;
import ch.ti8m.egov.framework.validation.util.ClassConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EGOV_CMD_Command")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Command extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;

    @Transient
    private String originalAction;

    private String aggregateName;

    @Convert(converter = ObjectConverter.class)
    private Object commandValue;

    private String version;

    @Enumerated(EnumType.STRING)
    private ExecutionPlan executionPlan;

    private String executingUserId;

    private Long aggregateId;

    private String domain;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Convert(converter = ObjectConverter.class)
    private Map<String, Object> info;

    @Column(length = 32000)
    private String error;

    private String absoluteRequestPath;

    @Transient
    private Object selectedRuleSet;

    @Transient
    @JsonIgnore
    private BaseExecutor executor;

    @Column(length = 4000)
    @Convert(converter = ParametersConverter.class)
    private Parameters parameters;

    @Transient
    private ValidationTriple validationResult;

    @Transient
    private Boolean validateValueAsMap;
    //, columnDefinition = "uniqueidentifier") not working with postgresql
    private String exceptionId;

    private Long parentid;

    // todo: without lazy fetch, hibernates results in deadlocks. reason not known exactly so far.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentid", referencedColumnName = "id", insertable = false, updatable = false)
    private Command parentCommand;

    @Builder.Default
    @OneToMany(mappedBy = "parentCommand", fetch = FetchType.LAZY)
    @OrderBy("modifiedDate DESC")

    private List<Command> childCommand = new ArrayList<>();

    @Transient
    @JsonIgnore
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Object nonSerializableContext;

    /*
     * Toggling this method means that the map result of the Validation Engine will be used.
     * This should be used whenever one wants to return Entity Classes directly without using DTOs or when Responses/Requests need to be filtered based on the Rulesets.
     */
    public void validateValueAsMap() {
        this.validateValueAsMap = true;
    }

    /*
     * Toggling this method means that the map result of the Validation Engine will be ignored and the original DTO value will be used.
     * This should be used whenever one wants to use typed DTO classes. They may be retrieved calling command.unwrap();
     */
    public void validateValueAsDto() {
        this.validateValueAsMap = false;
    }

    public Object execute() {
        if (executor == null) {
            throw new EGovException(ExceptionCode.NO_EXECUTOR_SET, "No executor set for command " + this.getId());
        }
        return executor.execute(this);
    }

    public RuleSet getSelectedRuleSet() {
        return (RuleSet) selectedRuleSet;
    }

    public Command getCopy() {
        final Command command = new Command();
        command.setAction(getAction());
        command.setAggregateName(getAggregateName());
        command.setCommandValue(getCommandValue());
        command.setVersion(getVersion());
        command.setExecutionPlan(getExecutionPlan());
        command.setExecutingUserId(getExecutingUserId());
        command.setSelectedRuleSet(getSelectedRuleSet());
        command.setExecutor(getExecutor());
        command.setParameters(getParameters());
        return command;
    }

    public <T> T unwrap() {
        return (T) commandValue;
    }

    public void pushNonSerializableContext(final Object context) {
        this.nonSerializableContext = context;
    }

    public <T> T popNonSerializableContext() {
        final var context = (T) this.nonSerializableContext;
        this.nonSerializableContext = null;
        return context;
    }

    public <T> T getAs(final String key) {
        return (T) ((Map<String, Object>) getCommandValue()).get(key);
    }

    public <T> T getAsOrDefault(final String key, final T defaultValue) {
        final T value = getAs(key);

        return value != null ? value : defaultValue;
    }

    public <T> T forClass(final Class<T> clazz) {
        return ClassConverter.forClass(clazz, commandValue);
    }

    public <T> T forClass(final TypeReference<T> typeReference) {
        return ClassConverter.forClass(typeReference, commandValue);
    }

    public <T> T forClass(final Class<T> clazz, final T entity) {
        return ClassConverter.forClass(clazz, commandValue, entity);
    }

    public <T> T forClass(final TypeReference<T> typeReference, final T entity) {
        return ClassConverter.forClass(typeReference, commandValue, entity);
    }

    public <T extends BaseAction> T getActionAs(final Class<T> enumType) {
        return Arrays.stream(enumType.getEnumConstants())
                .filter(enumValue -> enumValue.getAction().equals(action))
                .findFirst()
                .orElseThrow(() -> new EGovException(ExceptionCode.INVALID_ACTION,
                        "Action " + action + " not found in " + enumType.getSimpleName()));
    }

    public enum ExecutionPlan {
        VALIDATE_BEFORE_EXECUTING_ANYWAY,
        VALIDATE_BEFORE_EXECUTION,
        VALIDATE_AFTER_EXECUTION,
        DONT_VALIDATE,
        DONT_EXECUTE
    }

    public enum Status {
        INITIALIZED,
        RULESET_SELECTED,
        VALIDATED,
        COMPLETED,
        EXECUTED,
        HARD_VALIDATION_FAILED
    }

}
