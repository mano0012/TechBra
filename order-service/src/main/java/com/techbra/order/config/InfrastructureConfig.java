package com.techbra.order.config;

import com.techbra.order.domain.OrderRepository;
import com.techbra.order.infrastructure.persistence.OrderJpaRepository;
import com.techbra.order.infrastructure.persistence.OrderMapper;
import com.techbra.order.infrastructure.persistence.OrderRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuração de infraestrutura do Order Service
 * 
 * Esta classe configura os beans de infraestrutura necessários
 * para o funcionamento do serviço de pedidos, seguindo os
 * princípios da arquitetura hexagonal.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.techbra.order.infrastructure.persistence")
@EnableJpaAuditing
@EnableTransactionManagement
public class InfrastructureConfig {
    
    /**
     * Configura o mapper para conversão entre entidades de domínio e JPA
     * 
     * @return instância do OrderMapper
     */
    @Bean
    public OrderMapper orderMapper() {
        return new OrderMapper();
    }
    
    /**
     * Configura a implementação do repositório de pedidos
     * 
     * Este bean implementa a interface de domínio OrderRepository
     * usando a infraestrutura JPA.
     * 
     * @param orderJpaRepository repositório JPA
     * @param orderMapper mapper para conversão de entidades
     * @return implementação do repositório de domínio
     */
    @Bean
    public OrderRepository orderRepository(
            OrderJpaRepository orderJpaRepository,
            OrderMapper orderMapper) {
        return new OrderRepositoryImpl(orderJpaRepository, orderMapper);
    }
}