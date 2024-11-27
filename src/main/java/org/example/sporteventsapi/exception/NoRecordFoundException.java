package org.example.sporteventsapi.exception;

import org.springframework.http.HttpStatus;

public class NoRecordFoundException extends ApiException {
    public NoRecordFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("No %s was found by %s with value '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND);
    }
}
