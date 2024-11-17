//package org.example.sporteventsapi.deserializer;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import org.example.sporteventsapi.exception.EnumDeserializationException;
//
//import java.io.IOException;
//
//public class GenericEnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> {
//
//    private final Class<T> enumClass;
//
//    public GenericEnumDeserializer(Class<T> enumClass) {
//        this.enumClass = enumClass;
//    }
//
//    @Override
//    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//        String value = p.getText();
//        try {
//            return Enum.valueOf(enumClass, value.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            throw new EnumDeserializationException(enumClass.getSimpleName(), value);
//        }
//    }
//}
