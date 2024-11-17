package org.example.sporteventsapi;

import javax.validation.Payload;

public @interface ValidEnum {
    String message() default "Invalid value for enum";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<? extends Enum<?>> enumClass();
}
