//package org.example.sporteventsapi.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.example.sporteventsapi.deserializer.GenericEnumDeserializer;
//import org.example.sporteventsapi.model.SportEventStatus;
//import org.example.sporteventsapi.model.SportType;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class JacksonConfig {
//
//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        SimpleModule module = new SimpleModule();
//
//        module.addDeserializer(SportType.class, new GenericEnumDeserializer<>(SportType.class));
//        module.addDeserializer(SportEventStatus.class, new GenericEnumDeserializer<>(SportEventStatus.class));
//
//        objectMapper.registerModule(module);
//        return objectMapper;
//    }
//
////    @Override
////    public void extendMessageConverters(java.util.List<HttpMessageConverter<?>> converters) {
////        // Register the custom ObjectMapper with Spring
////        converters.add(new MappingJackson2HttpMessageConverter(objectMapper()));
////    }
//}
