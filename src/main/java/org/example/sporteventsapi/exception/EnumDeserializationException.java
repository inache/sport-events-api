package org.example.sporteventsapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class EnumDeserializationException extends RuntimeException {

    private final String enumName;
    private final String invalidValue;

    public EnumDeserializationException(String enumName, String invalidValue) {
        super("Invalid value '" + invalidValue + "' for enum " + enumName);
        this.enumName = enumName;
        this.invalidValue = invalidValue;
    }
}
