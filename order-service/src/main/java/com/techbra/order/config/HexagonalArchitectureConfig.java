package com.techbra.order.config;

import com.techbra.order.domain.service.OrderService;
import com.techbra.order.domain.ports.in.OrderUseCase;
import com.techbra.order.domain.ports.out.OrderRepositoryPort;
import com.techbra.order.infrastructure.adapters.OrderRepositoryAdapter;
import com.techbra.order.infrastructure.persistence.OrderJpaRepository;
import com.techbra.order.infrastructure.persistence.OrderMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da arquitetura hexagonal
 * 
 * Esta classe é responsável por conectar todas as camadas da arquitetura hexagonal,
 * configurando as dependências entre portas, adaptadores e o núcleo do domínio.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
@Configuration
public class HexagonalArchitectureConfig {
    
    /**
     * Configura o adaptador de saída para persistência
     * 
     * @param orderJpaRepository repositório JPA
     * @param orderMapper mapper entre domínio e entidade
     * @return implementação da porta de saída
     */
    @Bean
    public OrderRepositoryPort orderRepositoryPort(
            OrderJpaRepository orderJpaRepository,
            OrderMapper orderMapper) {
        return new OrderRepositoryAdapter(orderJpaRepository, orderMapper);
    }
    
    /**
     * Configura o serviço de domínio como implementação da porta de entrada
     * 
     * @param orderRepositoryPort porta de saída para persistência
     * @return implementação da porta de entrada
     */
    @Bean
    public OrderUseCase orderUseCase(OrderRepositoryPort orderRepositoryPort) {
        return new OrderService(orderRepositoryPort);
    }
}