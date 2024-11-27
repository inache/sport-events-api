package org.example.sporteventsapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final String VALIDATION_FAILED_MESSAGE = "Validation Failed!";
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred!";
    private static final String UNEXPECTED_ERROR_DETAIL = "An unexpected error occurred while processing your request!";

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException exception) {
        logError(exception);
        var errorResponse = buildErrorResponse(exception.getMessage(), exception.getStatus(), exception.getMessage(), null);
        return new ResponseEntity<>(errorResponse, exception.getStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of("field", error.getField(), "message", Objects.requireNonNull(error.getDefaultMessage())))
                .collect(Collectors.toList());

        var errorResponse = buildErrorResponse(VALIDATION_FAILED_MESSAGE, HttpStatus.BAD_REQUEST, "One or more fields are invalid!", fieldErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoRecordFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoRecordFoundException(NoRecordFoundException exception) {
        logError(exception);
        var errorResponse = buildErrorResponse(
                "No record found!",
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleEnumMismatch(MethodArgumentTypeMismatchException exception) {
        logError(exception);

        var detailMessage = String.format(
                "The provided value '%s' could not be converted to the expected type '%s'!",
                exception.getValue(),
                Objects.requireNonNull(exception.getRequiredType()).getSimpleName()
        );

        var errorResponse = buildErrorResponse(
                "Invalid value for parameter: " + exception.getName(),
                HttpStatus.BAD_REQUEST,
                detailMessage,
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        logError(exception);

        var detailMessage = "Invalid request body. ";
        if (exception.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException formatException) {
            detailMessage += String.format(
                    "Cannot deserialize value '%s' to the expected type. Accepted values: %s!",
                    formatException.getValue(),
                    formatException.getTargetType().getEnumConstants() != null ?
                            List.of(formatException.getTargetType().getEnumConstants()) :
                            "Unknown"
            );
        } else {
            detailMessage += exception.getMostSpecificCause().getMessage();
        }

        var errorResponse = buildErrorResponse(
                "Invalid Request Body",
                HttpStatus.BAD_REQUEST,
                detailMessage,
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception exception) {
        logError(exception);
        var errorResponse = buildErrorResponse(
                UNEXPECTED_ERROR_MESSAGE,
                HttpStatus.INTERNAL_SERVER_ERROR,
                UNEXPECTED_ERROR_DETAIL,
                null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logError(Exception ex) {
        log.error("An error occurred: {}", ex.getMessage(), ex);
    }

    private Map<String, Object> buildErrorResponse(String title, HttpStatus status, String detail, List<Map<String, String>> fieldErrors) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("title", title);
        errorResponse.put("status", status.value());
        errorResponse.put("detail", detail);
        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            errorResponse.put("errors", fieldErrors);
        }
        return errorResponse;
    }
}
