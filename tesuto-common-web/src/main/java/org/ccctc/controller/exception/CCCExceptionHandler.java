package org.ccctc.controller.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom {@link ResponseEntityExceptionHandler} that returns a REST response.
 */
@ControllerAdvice
@Slf4j
public class CCCExceptionHandler extends ResponseEntityExceptionHandler {
    @Data
    public class ExceptionRestResponse {
        private String message;
        private String status;
        private String uri;
    }

    private ExceptionRestResponse createResponse(HttpStatus status, WebRequest request, Exception ex) {
        final ExceptionRestResponse response = new ExceptionRestResponse();
        response.setUri(getUri(request));
        response.setStatus(String.valueOf(status.value()));
        response.setMessage(ex.getLocalizedMessage());
        return response;
    }

    private String getUri(WebRequest request) {
        final String desc = request.getDescription(false);
        return (desc.startsWith("uri=")) ? desc.substring(4) : "";
    }

    /**
     * Result: HttpStatus.FORBIDDEN (403)
     */
    @ExceptionHandler(value = { AccessDeniedException.class })
    public ResponseEntity<Object> handleAccessDenied(RuntimeException ex, WebRequest request) {
        return this.handleException(ex, request, HttpStatus.FORBIDDEN, null);
    }

    protected ResponseEntity<Object> handleException(Exception ex, WebRequest request, HttpStatus status, ExceptionRestResponse response) {
        return handleExceptionInternal(ex, response, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final Object bodyToUse = (body == null) ? this.createResponse(status, request, ex) : body;
        log.info("Handling exception: {}", ex);
        log.debug("Returning: {}", body);
        return super.handleExceptionInternal(ex, bodyToUse, headers, status, request);
    }

    /**
     * Result: HttpStatus.INTERNAL_SERVER_ERROR (500)
     */
    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
        return this.handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, null);
    }
}
