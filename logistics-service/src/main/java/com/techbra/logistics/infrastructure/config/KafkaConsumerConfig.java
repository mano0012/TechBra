package com.techbra.logistics.infrastructure.config;

import com.techbra.logistics.domain.events.LogisticsEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração do Kafka Consumer para receber eventos de logística
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${app.kafka.consumer.group-id:logistics-service-group}")
    private String groupId;

    /**
     * Configuração do Consumer Factory para LogisticsEvent
     */
    @Bean
    public ConsumerFactory<String, LogisticsEvent> logisticsEventConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Configurações básicas
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        
        // Configurações de deserialização JSON
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.techbra.logistics.domain.events");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LogisticsEvent.class.getName());
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        
        // Configurações de offset e commit
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        
        // Configurações de performance
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        
        // Configurações de timeout
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Container Factory para LogisticsEvent
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LogisticsEvent> logisticsEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LogisticsEvent> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(logisticsEventConsumerFactory());
        
        // Configurações do container
        factory.setConcurrency(1); // Número de threads consumidoras
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setSyncCommits(true);
        
        // Configurações de retry e error handling
        factory.setCommonErrorHandler(null); // Pode ser configurado posteriormente
        
        return factory;
    }
}