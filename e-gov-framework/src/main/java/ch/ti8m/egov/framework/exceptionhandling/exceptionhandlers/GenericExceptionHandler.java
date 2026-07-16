package ch.ti8m.egov.framework.exceptionhandling.exceptionhandlers;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.exceptionhandling.model.ActionNotAllowedException;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.exceptionhandling.model.NoPermissionForEntityException;
import ch.ti8m.egov.framework.exceptionhandling.model.ResponseExceptionBody;
import ch.ti8m.egov.framework.exceptionhandling.model.SystemException;
import ch.ti8m.egov.framework.exceptionhandling.model.UnauthorizedException;
import ch.ti8m.egov.framework.exceptionhandling.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.UUID;

@Slf4j
@ControllerAdvice
public class GenericExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String MESSAGE = "Internal server error";

    @Value("${egov.exception-log.debug:false}")
    private boolean debug;

    @ExceptionHandler(value = {ActionNotAllowedException.class})
    public ResponseEntity<Object> handleActionNotAllowedException(final ActionNotAllowedException e, final WebRequest request) {
        DataHolder.setDebugPermission(debug);
        return handleExceptionInternal(e, getEgovExceptionBody(e), new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    @ExceptionHandler(value = {ClassNotFoundException.class})
    public ResponseEntity<Object> handleClassNotFoundException(final ClassNotFoundException e, final WebRequest request) {
        DataHolder.setDebugPermission(debug);
        return handleExceptionInternal(e, getExceptionBody(e), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {EGovException.class})
    public ResponseEntity<Object> handleEgovException(final EGovException e, final WebRequest request) {
        DataHolder.setDebugPermission(debug);
        return handleExceptionInternal(e, getEgovExceptionBody(e), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {NoPermissionForEntityException.class})
    public ResponseEntity<Object> handleNoPermissionForEntityException(final NoPermissionForEntityException e, final WebRequest request) {
        DataHolder.setDebugPermission(debug);
        return handleExceptionInternal(e, getEgovExceptionBody(e), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(final RuntimeException e, final WebRequest request) {
        DataHolder.setDebugPermission(debug);
        return handleExceptionInternal(e, getExceptionBody(e), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {SystemException.class})
    public ResponseEntity<Object> handleSystemException(final SystemException e, final WebRequest request) {
        DataHolder.setDebugPermission(debug);
        return handleExceptionInternal(e, getEgovExceptionBody(e), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(final UnauthorizedException e, final WebRequest request) {
        DataHolder.setDebugPermission(debug);
        return handleExceptionInternal(e, getEgovExceptionBody(e), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    protected ResponseExceptionBody getExceptionBody(
            final Exception e
    ) {
        final UUID exceptionId = DataHolder.getExceptionId();
        final boolean debugPermission = Boolean.TRUE.equals(DataHolder.getDebugPermission());

        GenericExceptionHandler.log.debug("Exception thrown {}: {}", DataHolder.getExceptionId(), e.getMessage(), e);

        final ResponseExceptionBody.ResponseExceptionBodyBuilder responseExceptionBodyBuilder =
                ResponseExceptionBody.builder()
                        .message(GenericExceptionHandler.MESSAGE)
                        .exceptionId(exceptionId)
                        .errorCode(ExceptionCode.DEFAULT)
                        .link(ExceptionUtils.getLink(exceptionId));

        if (debugPermission) {
            responseExceptionBodyBuilder.stackTrace(ExceptionUtils.getStackTrace(e));
        } else {
            responseExceptionBodyBuilder.stackTrace("Stacktrace omitted.");
        }

        return responseExceptionBodyBuilder.build();
    }

    protected ResponseExceptionBody getEgovExceptionBody(
            final EGovException e
    ) {
        final UUID exceptionId = DataHolder.getExceptionId();
        final boolean debugPermission = Boolean.TRUE.equals(DataHolder.getDebugPermission());

        GenericExceptionHandler.log.debug("Exception thrown {}: {}, {}", exceptionId, e.getCode(), e.getMessage(), e);

        final ResponseExceptionBody.ResponseExceptionBodyBuilder responseExceptionBodyBuilder =
                ResponseExceptionBody.builder()
                        .message(e.getMessage())
                        .exceptionId(exceptionId)
                        .errorCode(e.getCode())
                        .additionalInfo(e.getAdditionalInfo())
                        .link(ExceptionUtils.getLink(exceptionId));

        if (debugPermission) {
            responseExceptionBodyBuilder.stackTrace(ExceptionUtils.getStackTrace(e));
        } else {
            responseExceptionBodyBuilder.stackTrace("Stacktrace omitted.");
        }

        return responseExceptionBodyBuilder.build();
    }
}
