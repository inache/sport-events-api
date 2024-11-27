package org.example.sporteventsapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {
    private final HttpStatus status;

    ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
