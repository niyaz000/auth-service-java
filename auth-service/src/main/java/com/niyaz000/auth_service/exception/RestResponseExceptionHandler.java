package com.niyaz000.auth_service.exception;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.niyaz000.auth_service.constants.ApiConstants;
import com.niyaz000.auth_service.utils.LoggerUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateEntityException(
            DuplicateEntityException ex,
            HttpServletRequest request) {
        var response = toResponseBuilder(HttpStatus.BAD_REQUEST.value())
                .type(ApiConstants.DUPLICATE_VALIDATION_ERROR)
                .detail(String.format("Entity with given primary identifier '%s' already exists.", ex.getField()))
                .title("Validation Error")
                .instance(ApiConstants.ENTITY_API_PATHS.get(ex.getEntity()))
                .errors(List.of(new ApiErrorResponse.FieldError(ex.getField(), ex.getValue(),
                        ApiConstants.DUPLICATE_VALIDATION_ERROR,
                        String.format("Entity with given primary identifier '%s' already exists.", ex.getField()))))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex,
            HttpServletRequest request) {
        var response = toResponseBuilder(HttpStatus.NOT_FOUND.value())
                .type(ApiConstants.ENTITY_NOT_FOUND_ERROR)
                .detail(String.format("Entity with id '%s' not found.", ex.getIdentifierValue()))
                .title("Entity Not Found")
                .instance(ApiConstants.ENTITY_API_PATHS.get(ex.getEntityName()))
                .errors(List.of(new ApiErrorResponse.FieldError("id", ex.getIdentifierName().toString(),
                        ApiConstants.ENTITY_NOT_FOUND_ERROR,
                        String.format("Entity type '%s' with id '%s' not found.",
                                ex.getEntityName().name().toLowerCase(), ex.getIdentifierValue()))))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleInternalServerError(HttpServletRequest request,
            Exception ex) {
        log.error("Internal server error occurred: ", ex);
        var response = toResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .type(ApiConstants.INTERNAL_SERVER_ERROR)
                .instance(request.getServletPath().replace("/v1", ""))
                .detail("An unexpected error occurred. Please try again later. Please contact support if the issue persists.")
                .title("Internal Server Error")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        var response = toResponseBuilder(HttpStatus.BAD_REQUEST.value())
                .type(ApiConstants.VALIDATION_ERROR)
                .detail(String.format("Invalid value '%s' for parameter '%s'.", ex.getValue(), ex.getName()))
                .title("Validation Error")
                .instance(request.getServletPath().replace("/v1", ""))
                .errors(List.of(new ApiErrorResponse.FieldError(ex.getName(),
                        ex.getValue() != null ? ex.getValue().toString() : "null", ApiConstants.VALIDATION_ERROR,
                        String.format("Invalid value '%s' for parameter '%s'.", ex.getValue(), ex.getName()))))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        var response = toResponseBuilder(HttpStatus.BAD_REQUEST.value())
                .type(ApiConstants.VALIDATION_ERROR)
                .detail("One or more validation errors occurred.")
                .title("Validation Error")
                .instance(request.getServletPath().replace("/v1", ""))
                .errors(ex.getConstraintViolations().stream().map(violation -> toFieldError(violation)).toList())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ApiErrorResponse.ApiErrorResponseBuilder toResponseBuilder(int status) {
        return new ApiErrorResponse.ApiErrorResponseBuilder()
                .requestId(LoggerUtil.currentRequestId())
                .timestamp(Date.from(Instant.now()))
                .status(status);
    }

    private ApiErrorResponse.FieldError toFieldError(ConstraintViolation<?> violation) {
        var property = violation.getPropertyPath().toString().split("/");
        return new ApiErrorResponse.FieldError(
                property[property.length - 1],
                violation.getInvalidValue() != null ? violation.getInvalidValue().toString() : "null",
                ApiConstants.VALIDATION_ERROR,
                violation.getMessage());
    }

}
