package com.techbra.order.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface do repositório de pedidos (Port)
 * 
 * Define as operações de persistência para a entidade Order
 * seguindo os princípios da Arquitetura Hexagonal.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public interface OrderRepository {
    
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
     * Busca pedidos criados em um período
     * 
     * @param startDate data de início
     * @param endDate data de fim
     * @return lista de pedidos criados no período
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Busca pedidos de um cliente criados em um período
     * 
     * @param customerId o ID do cliente
     * @param startDate data de início
     * @param endDate data de fim
     * @return lista de pedidos do cliente criados no período
     */
    List<Order> findByCustomerIdAndCreatedAtBetween(UUID customerId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Busca todos os pedidos com paginação
     * 
     * @param page número da página (começando em 0)
     * @param size tamanho da página
     * @return lista paginada de pedidos
     */
    List<Order> findAll(int page, int size);
    
    /**
     * Busca pedidos de um cliente com paginação
     * 
     * @param customerId o ID do cliente
     * @param page número da página (começando em 0)
     * @param size tamanho da página
     * @return lista paginada de pedidos do cliente
     */
    List<Order> findByCustomerId(UUID customerId, int page, int size);
    
    /**
     * Conta o total de pedidos
     * 
     * @return número total de pedidos
     */
    long count();
    
    /**
     * Conta o total de pedidos de um cliente
     * 
     * @param customerId o ID do cliente
     * @return número total de pedidos do cliente
     */
    long countByCustomerId(UUID customerId);
    
    /**
     * Conta pedidos por status
     * 
     * @param status o status dos pedidos
     * @return número de pedidos com o status especificado
     */
    long countByStatus(OrderStatus status);
    
    /**
     * Verifica se existe um pedido com o número especificado
     * 
     * @param orderNumber o número do pedido
     * @return true se existe, false caso contrário
     */
    boolean existsByOrderNumber(String orderNumber);
    
    /**
     * Deleta um pedido por ID
     * 
     * @param id o ID do pedido
     */
    void deleteById(UUID id);
    
    /**
     * Deleta um pedido
     * 
     * @param order o pedido a ser deletado
     */
    void delete(Order order);
    
    /**
     * Busca pedidos que precisam ser processados automaticamente
     * (ex: pedidos pendentes há muito tempo)
     * 
     * @param olderThan data limite para considerar pedidos antigos
     * @return lista de pedidos que precisam ser processados
     */
    List<Order> findPendingOrdersOlderThan(LocalDateTime olderThan);
    
    /**
     * Busca pedidos por múltiplos status
     * 
     * @param statuses lista de status
     * @return lista de pedidos com qualquer um dos status especificados
     */
    List<Order> findByStatusIn(List<OrderStatus> statuses);
    
    /**
     * Busca os últimos pedidos de um cliente
     * 
     * @param customerId o ID do cliente
     * @param limit número máximo de pedidos a retornar
     * @return lista dos últimos pedidos do cliente
     */
    List<Order> findLatestByCustomerId(UUID customerId, int limit);
}