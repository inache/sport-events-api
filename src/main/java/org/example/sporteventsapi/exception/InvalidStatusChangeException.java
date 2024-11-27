package org.example.sporteventsapi.exception;

import org.example.sporteventsapi.model.SportEventStatus;
import org.springframework.http.HttpStatus;

public class InvalidStatusChangeException extends ApiException {
    public InvalidStatusChangeException(SportEventStatus currentStatus, SportEventStatus attemptedStatus) {
        super(String.format("Invalid status change from '%s' to '%s'", currentStatus, attemptedStatus), HttpStatus.BAD_REQUEST);
    }

    public InvalidStatusChangeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
