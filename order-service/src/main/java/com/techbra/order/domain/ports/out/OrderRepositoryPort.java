package com.techbra.order.domain.ports.out;

import com.techbra.order.domain.Order;
import com.techbra.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface que define as operações de persistência para pedidos
 * 
 * Esta interface representa a porta de saída (outbound port) da arquitetura hexagonal,
 * definindo as operações de persistência necessárias para o domínio de pedidos.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public interface OrderRepositoryPort {
    
    /**
     * Salva um pedido
     * 
     * @param order o pedido a ser salvo
     * @return o pedido salvo com ID gerado
     */
    Order save(Order order);
    
    /**
     * Busca um pedido por ID
     * 
     * @param id o ID do pedido
     * @return Optional contendo o pedido se encontrado
     */
    Optional<Order> findById(UUID id);
    
    /**
     * Busca um pedido por número do pedido
     * 
     * @param orderNumber o número do pedido
     * @return Optional contendo o pedido se encontrado
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * Busca todos os pedidos de um cliente
     * 
     * @param customerId o ID do cliente
     * @return lista de pedidos do cliente
     */
    List<Order> findByCustomerId(UUID customerId);
    
    /**
     * Busca pedidos por status
     * 
     * @param status o status dos pedidos
     * @return lista de pedidos com o status especificado
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Busca pedidos de um cliente por status
     * 
     * @param customerId o ID do cliente
     * @param status o status dos pedidos
     * @return lista de pedidos do cliente com o status especificado
     */
    List<Order> findByCustomerIdAndStatus(UUID customerId, OrderStatus status);
    
    /**
     * Busca pedidos criados em um período específico
     * 
     * @param startDate data inicial (inclusive)
     * @param endDate data final (inclusive)
     * @return lista de pedidos criados no período
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Busca pedidos de um cliente criados em um período específico
     * 
     * @param customerId o ID do cliente
     * @param startDate data inicial (inclusive)
     * @param endDate data final (inclusive)
     * @return lista de pedidos do cliente criados no período
     */
    List<Order> findByCustomerIdAndCreatedAtBetween(UUID customerId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Calcula o valor total dos pedidos de um cliente
     * 
     * @param customerId o ID do cliente
     * @return valor total dos pedidos
     */
    BigDecimal calculateTotalSalesByCustomerId(UUID customerId);
    
    /**
     * Conta o número de pedidos de um cliente
     * 
     * @param customerId o ID do cliente
     * @return número de pedidos
     */
    Long countByCustomerId(UUID customerId);
    
    /**
     * Conta o número de pedidos por status
     * 
     * @param status o status dos pedidos
     * @return número de pedidos com o status especificado
     */
    Long countByStatus(OrderStatus status);
    
    /**
     * Verifica se existe um pedido com o número especificado
     * 
     * @param orderNumber o número do pedido
     * @return true se existe, false caso contrário
     */
    boolean existsByOrderNumber(String orderNumber);
    
    /**
     * Remove um pedido por ID
     * 
     * @param id o ID do pedido a ser removido
     */
    void deleteById(UUID id);
    
    /**
     * Busca todos os pedidos
     * 
     * @return lista de todos os pedidos
     */
    List<Order> findAll();
    
    /**
     * Busca pedidos com paginação
     * 
     * @param page número da página (começando em 0)
     * @param size tamanho da página
     * @return lista paginada de pedidos
     */
    List<Order> findAllPaginated(int page, int size);
    
    /**
     * Busca pedidos de um cliente com paginação
     * 
     * @param customerId o ID do cliente
     * @param page número da página (começando em 0)
     * @param size tamanho da página
     * @return lista paginada de pedidos do cliente
     */
    List<Order> findByCustomerIdPaginated(UUID customerId, int page, int size);
    
    /**
     * Busca pedidos de um cliente com paginação (método alternativo)
     * 
     * @param customerId o ID do cliente
     * @param page número da página (começando em 0)
     * @param size tamanho da página
     * @return lista paginada de pedidos do cliente
     */
    List<Order> findOrdersByCustomerIdPaginated(UUID customerId, int page, int size);
    
    /**
     * Busca pedidos ativos de um cliente
     * 
     * @param customerId o ID do cliente
     * @return lista de pedidos ativos do cliente
     */
    List<Order> findActiveOrdersByCustomerId(UUID customerId);
    
    /**
     * Busca pedidos canceláveis de um cliente
     * 
     * @param customerId o ID do cliente
     * @return lista de pedidos canceláveis do cliente
     */
    List<Order> findCancellableOrdersByCustomerId(UUID customerId);
    
    /**
     * Conta o número de pedidos por status
     * 
     * @param status o status dos pedidos
     * @return número de pedidos com o status especificado
     */
    Long countOrdersByStatus(OrderStatus status);
}