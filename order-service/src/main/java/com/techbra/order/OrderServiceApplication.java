package com.techbra.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Aplicação principal do Order Service
 * 
 * Microserviço responsável pelo gerenciamento de pedidos no sistema TechBra.
 * Fornece funcionalidades para criação, consulta, atualização e cancelamento de pedidos.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}