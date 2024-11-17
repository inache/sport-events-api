package org.example.sporteventsapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final String NO_RECORD_FOUND_MESSAGE = "No record found!";
    private static final String TYPE_MISMATCH_MESSAGE = "Invalid value for parameter: ";
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred!";
    private static final String UNEXPECTED_ERROR_DETAIL = "An unexpected error occurred while processing your request!";

    @ExceptionHandler(NoRecordFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoRecordFoundException(NoRecordFoundException exception) {
        logError(exception);
        var errorResponse = buildErrorResponse(NO_RECORD_FOUND_MESSAGE, HttpStatus.NOT_FOUND,
                exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleEnumMismatch(MethodArgumentTypeMismatchException exception) {
        logError(exception);
        var errorResponse = buildErrorResponse(TYPE_MISMATCH_MESSAGE + exception.getName(), HttpStatus.BAD_REQUEST,
                "The provided value '" + exception.getValue() + "' could not be converted to the expected type " + Objects.requireNonNull(exception.getRequiredType()).getSimpleName());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EnumDeserializationException.class)
    public ResponseEntity<Map<String, Object>> handleEnumDeserializationException(EnumDeserializationException exception) {
        logError(exception);
        var errorResponse = buildErrorResponse(
                "Invalid value for enum " + exception.getEnumName(),
                HttpStatus.BAD_REQUEST,
                "The provided value '" + exception.getInvalidValue() + "' is invalid for enum "
                        + exception.getEnumName()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception exception) {
        logError(exception);
        var errorResponse = buildErrorResponse(UNEXPECTED_ERROR_MESSAGE,
                HttpStatus.INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR_DETAIL);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logError(Exception ex) {
        log.error("An error occurred: {}", ex.getMessage(), ex);
    }

    private Map<String, Object> buildErrorResponse(String title, HttpStatus status, String detail) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("title", title);
        errorResponse.put("status", status.value());
        errorResponse.put("detail", detail);
        return errorResponse;
    }
}
