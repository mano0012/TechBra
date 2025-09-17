package com.techbra.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.format.DateTimeFormatter;

/**
 * Configuração do Jackson para serialização/deserialização JSON
 * 
 * Esta classe configura o ObjectMapper do Jackson para padronizar
 * a forma como os objetos são convertidos para/de JSON.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
@Configuration
public class JacksonConfig {
    
    /**
     * Configura o ObjectMapper principal da aplicação
     * 
     * @param builder builder do Jackson2ObjectMapper
     * @return ObjectMapper configurado
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
    }
    
    /**
     * Configura o builder do Jackson2ObjectMapper
     * 
     * @return Jackson2ObjectMapperBuilder configurado
     */
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .failOnUnknownProperties(false)
                .failOnEmptyBeans(false);
    }
}