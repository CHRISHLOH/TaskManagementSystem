package com.task.management.system.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    private List<String> getDefaultErrorList(Exception ex) {
        return ex.getMessage() != null ? List.of(ex.getMessage()) : Collections.emptyList();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        String message = messageSource.getMessage(
                ex.getMessageCode(), ex.getArgs(),
                "Entity not found", LocaleContextHolder.getLocale());
        assert message != null;
        return new ErrorResponse(message, List.of(message), LocalDateTime.now());
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServiceException(ServiceException ex) {
        String message = messageSource.getMessage(
                ex.getMessageCode(), ex.getArgs(),
                "Service error occurred", LocaleContextHolder.getLocale());
        log.error("Service exception: {} with code: {}", message, ex.getMessageCode(), ex);

        String resolvedError = messageSource.getMessage(
                ex.getMessageCode(), ex.getArgs(),
                ex.getMessageCode(), LocaleContextHolder.getLocale());

        assert resolvedError != null;
        return new ErrorResponse(message, List.of(resolvedError), LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> messageSource.getMessage(
                        Objects.requireNonNull(error.getDefaultMessage()), null, error.getDefaultMessage(), LocaleContextHolder.getLocale()))
                .collect(Collectors.toList());

        String message = messageSource.getMessage(
                "validation.error", null, "Validation failed", LocaleContextHolder.getLocale());
        log.warn("Validation error: {}", errors);
        return new ErrorResponse(message, errors, LocalDateTime.now());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> messageSource.getMessage(
                        violation.getMessage(), null, violation.getMessage(), LocaleContextHolder.getLocale()))
                .collect(Collectors.toList());

        String message = messageSource.getMessage(
                "validation.error", null, "Validation failed", LocaleContextHolder.getLocale());
        log.warn("Constraint violation: {}", errors);
        return new ErrorResponse(message, errors, LocalDateTime.now());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = messageSource.getMessage(
                "message.not.readable", null, "Invalid request format", LocaleContextHolder.getLocale());
        log.warn("Message not readable: {}", ex.getMessage());
        return new ErrorResponse(message, getDefaultErrorList(ex), LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        String message = messageSource.getMessage(
                "error.general", null, "An unexpected error occurred", LocaleContextHolder.getLocale());
        log.error("Unexpected exception: ", ex);
        return new ErrorResponse(message, getDefaultErrorList(ex), LocalDateTime.now());
    }
}
