package ch.ti8m.egov.framework.exceptionhandling.model;

public final class ExceptionCode {


    private static final String EXCEPTION_PREFIX = "EGOV_";
    private static final String FRAMEWORK_PREFIX = ExceptionCode.EXCEPTION_PREFIX + "FRM_";

    public static final String DEFAULT = ExceptionCode.FRAMEWORK_PREFIX + "0000";
    public static final String INVALID_DATA = ExceptionCode.FRAMEWORK_PREFIX + "0001";
    public static final String FIND_BY_PARAMS_ERROR = ExceptionCode.FRAMEWORK_PREFIX + "0002";
    public static final String FIELD_NOT_FOUND_IN_CLASS = ExceptionCode.FRAMEWORK_PREFIX + "0003";
    public static final String OPERATION_NOT_PERMITTED = ExceptionCode.FRAMEWORK_PREFIX + "0004";
    public static final String COMPILER_ERROR = ExceptionCode.FRAMEWORK_PREFIX + "0005";
    public static final String FILTER_OPERATION_NOT_PERMITTED = ExceptionCode.FRAMEWORK_PREFIX + "0006";
    public static final String ID_FIELD_NOT_EXTRACTABLE = ExceptionCode.FRAMEWORK_PREFIX + "0007";
    public static final String ILLEGAL_USE_OF_SAVE = ExceptionCode.FRAMEWORK_PREFIX + "0008";
    public static final String UPDATE_FORBIDDEN = ExceptionCode.FRAMEWORK_PREFIX + "0009";
    public static final String CANNOT_CLONE_OBJECT = ExceptionCode.FRAMEWORK_PREFIX + "0010";
    public static final String ACTION_NOT_ALLOWED = ExceptionCode.FRAMEWORK_PREFIX + "0011";
    public static final String NO_PERMISSION_FOR_ENTITY = ExceptionCode.FRAMEWORK_PREFIX + "0012";
    public static final String NO_EXECUTOR_SET = ExceptionCode.FRAMEWORK_PREFIX + "0013";
    public static final String INVALID_ACTION = ExceptionCode.FRAMEWORK_PREFIX + "0014";
    public static final String ZOOKEEPER_ERROR = ExceptionCode.FRAMEWORK_PREFIX + "0015";
    public static final String ERROR_PROCESSING_VALIDATION_ENTITY = ExceptionCode.FRAMEWORK_PREFIX + "0016";
    public static final String SORT_NOT_ALLOWED = ExceptionCode.FRAMEWORK_PREFIX + "0017";
    public static final String COULD_NOT_READ_VALUE = ExceptionCode.FRAMEWORK_PREFIX + "0018";
    public static final String REMOTE_COMMAND_INVOCATION_EXCEPTION = ExceptionCode.FRAMEWORK_PREFIX + "0019";

    private static final String PERSISTENCE_PREFIX = ExceptionCode.FRAMEWORK_PREFIX + "PRS_";
    public static final String ID_LIMIT_EXCEEDED = ExceptionCode.PERSISTENCE_PREFIX + "0000";
    public static final String ILLEGAL_FILTER_PARAM_VALUE = ExceptionCode.PERSISTENCE_PREFIX + "0001";
    public static final String FILTER_PARAMS_INVALID = ExceptionCode.PERSISTENCE_PREFIX + "0002";

    private ExceptionCode() {
    }

}
