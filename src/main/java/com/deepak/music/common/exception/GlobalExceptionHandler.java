package com.deepak.music.common.exception;

import jakarta.validation.ConstraintViolationException;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Central exception handler for all REST controllers.
 *
 * <p>Maps domain and infrastructure exceptions to RFC 9457 {@link ProblemDetail} responses,
 * ensuring a consistent error shape across all endpoints. All messages are resolved through
 * {@link MessageSource} to support i18n via the {@code Accept-Language} header.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ApiException.class)
    public ProblemDetail handleApiException(ApiException ex, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage(
                ex.getMessageKey(),
                ex.getMessageArgs(),
                ex.getMessageKey(),
                locale
        );
        log.warn("API exception handled: errorCode={} messageKey={}", ex.getErrorCode(), ex.getMessageKey());
        return buildProblem(
                HttpStatus.valueOf(ex.getHttpStatus()),
                ex.getErrorCode(),
                errorMessage,
                request
        );
    }

    /** Handles stale-version writes; returns {@code 409 Conflict}. */
    @ExceptionHandler({OptimisticLockingException.class, StaleObjectStateException.class})
    public ProblemDetail handleOptimisticLockingException(Exception ex, WebRequest request) {
        log.warn("Optimistic locking conflict detected", ex);
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage("optimistic.lock.conflict", null, 
                "Resource was modified by another request", locale);
        return buildProblem(
                HttpStatus.CONFLICT,
                "CONFLICT",
                errorMessage,
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation failed for request: {}", ex.getMessage());
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage("validation.request_failed", null, locale);
        ProblemDetail problemDetail = buildProblem(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                errorMessage,
                request
        );

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        problemDetail.setProperty("errors", fieldErrors);

        return problemDetail;
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class,
            ConstraintViolationException.class
    })
    public ProblemDetail handleBadRequest(Exception ex, WebRequest request) {
        log.warn("Bad request handled: {}", ex.getMessage());
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage("common.bad_request", null, locale);
        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                errorMessage,
                request
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFound(NoResourceFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getResourcePath());
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage("common.not_found", null, "Resource not found", locale);
        return buildProblem(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                errorMessage,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage("common.internal_error", null, locale);
        return buildProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                errorMessage,
                request
        );
    }

    private ProblemDetail buildProblem(HttpStatus status, String title, String detail, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC));

        if(request instanceof ServletWebRequest servletWebRequest) {
            problemDetail.setProperty("path", servletWebRequest.getRequest().getRequestURI());
        }

        String correlationId = MDC.get("correlationId");
        if(correlationId != null) {
            problemDetail.setProperty("correlationId", correlationId);
        }
        return problemDetail;
    }

}
