package com.task.management.system.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        String message = messageSource.getMessage(ex.getMessageCode(), ex.getArgs(),
                "Entity not found", LocaleContextHolder.getLocale());

        log.warn("Entity not found: {} with code: {}", message, ex.getMessageCode());
        return new ErrorResponse(message);
    }

//    @ExceptionHandler(AccessDeniedException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
//        String message = messageSource.getMessage("access.denied", null,
//                "Access denied", LocaleContextHolder.getLocale());
//
//        log.warn("Access denied: {}", ex.getMessage());
//        return new ErrorResponse(message);
//    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServiceException(ServiceException ex) {
        String message = messageSource.getMessage(ex.getMessageCode(), ex.getArgs(),
                "Service error occurred", LocaleContextHolder.getLocale());

        ErrorResponse response = new ErrorResponse(message);
        log.error("Service exception: {} with code: {}", message, ex.getMessageCode(), ex);
        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> messageSource.getMessage(Objects.requireNonNull(error.getDefaultMessage()), null,
                        error.getDefaultMessage(), LocaleContextHolder.getLocale()))
                .toList();

        String message = messageSource.getMessage("validation.error", null,
                "Validation failed", LocaleContextHolder.getLocale());

        ErrorResponse response = new ErrorResponse(message);
        response.setErrors(errors);
        log.warn("Validation error: {}", errors);
        return response;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> messageSource.getMessage(violation.getMessage(), null,
                        violation.getMessage(), LocaleContextHolder.getLocale()))
                .toList();

        String message = messageSource.getMessage("validation.error", null,
                "Validation failed", LocaleContextHolder.getLocale());

        ErrorResponse response = new ErrorResponse(message);
        response.setErrors(errors);
        log.warn("Constraint violation: {}", errors);
        return response;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = messageSource.getMessage("data.integrity.violation", null,
                "Data integrity violation", LocaleContextHolder.getLocale());

        log.error("Data integrity violation: {}", ex.getMessage());
        return new ErrorResponse(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = messageSource.getMessage("message.not.readable", null,
                "Invalid request format", LocaleContextHolder.getLocale());

        log.warn("Message not readable: {}", ex.getMessage());
        return new ErrorResponse(message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        String message = messageSource.getMessage("error.general", null,
                "An unexpected error occurred", LocaleContextHolder.getLocale());

        ErrorResponse response = new ErrorResponse(message);
        log.error("Unexpected exception: ", ex);
        return response;
    }
}