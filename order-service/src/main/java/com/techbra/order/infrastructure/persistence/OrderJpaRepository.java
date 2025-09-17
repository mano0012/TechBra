package com.techbra.order.infrastructure.persistence;

import com.techbra.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA para a entidade OrderEntity
 * 
 * Esta interface estende JpaRepository e fornece métodos de acesso
 * aos dados da entidade Order na base de dados PostgreSQL.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    
    /**
     * Busca um pedido pelo número do pedido
     * 
     * @param orderNumber o número único do pedido
     * @return Optional contendo o pedido se encontrado
     */
    Optional<OrderEntity> findByOrderNumber(String orderNumber);
    
    /**
     * Busca todos os pedidos de um cliente específico
     * 
     * @param customerId o ID do cliente
     * @return lista de pedidos do cliente
     */
    List<OrderEntity> findByCustomerId(UUID customerId);
    
    /**
     * Busca pedidos de um cliente com paginação
     * 
     * @param customerId o ID do cliente
     * @param pageable configuração de paginação
     * @return página de pedidos do cliente
     */
    Page<OrderEntity> findByCustomerId(UUID customerId, Pageable pageable);
    
    /**
     * Busca pedidos por status
     * 
     * @param status o status do pedido
     * @return lista de pedidos com o status especificado
     */
    List<OrderEntity> findByStatus(OrderStatus status);
    
    /**
     * Busca pedidos por status com paginação
     * 
     * @param status o status do pedido
     * @param pageable configuração de paginação
     * @return página de pedidos com o status especificado
     */
    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);
    
    /**
     * Busca pedidos de um cliente com status específico
     * 
     * @param customerId o ID do cliente
     * @param status o status do pedido
     * @return lista de pedidos do cliente com o status especificado
     */
    List<OrderEntity> findByCustomerIdAndStatus(UUID customerId, OrderStatus status);
    
    /**
     * Busca pedidos criados em um período específico
     * 
     * @param startDate data de início
     * @param endDate data de fim
     * @return lista de pedidos criados no período
     */
    List<OrderEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Busca pedidos criados em um período específico com paginação
     * 
     * @param startDate data de início
     * @param endDate data de fim
     * @param pageable configuração de paginação
     * @return página de pedidos criados no período
     */
    Page<OrderEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Busca pedidos de um cliente criados em um período específico
     * 
     * @param customerId o ID do cliente
     * @param startDate data de início
     * @param endDate data de fim
     * @return lista de pedidos do cliente criados no período
     */
    List<OrderEntity> findByCustomerIdAndCreatedAtBetween(UUID customerId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Conta o número de pedidos de um cliente
     * 
     * @param customerId o ID do cliente
     * @return número de pedidos do cliente
     */
    long countByCustomerId(UUID customerId);
    
    /**
     * Conta o número de pedidos por status
     * 
     * @param status o status do pedido
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
     * Busca pedidos ativos (não cancelados nem finalizados) de um cliente
     * 
     * @param customerId o ID do cliente
     * @return lista de pedidos ativos do cliente
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.customerId = :customerId AND o.status NOT IN ('CANCELLED', 'DELIVERED', 'RETURNED')")
    List<OrderEntity> findActiveOrdersByCustomerId(@Param("customerId") UUID customerId);
    
    /**
     * Busca pedidos que podem ser cancelados
     * 
     * @param customerId o ID do cliente
     * @return lista de pedidos que podem ser cancelados
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.customerId = :customerId AND o.status IN ('PENDING', 'CONFIRMED', 'PROCESSING')")
    List<OrderEntity> findCancellableOrdersByCustomerId(@Param("customerId") UUID customerId);
    
    /**
     * Busca pedidos por múltiplos status
     * 
     * @param statuses lista de status
     * @return lista de pedidos com os status especificados
     */
    List<OrderEntity> findByStatusIn(List<OrderStatus> statuses);
    
    /**
     * Busca pedidos por múltiplos status com paginação
     * 
     * @param statuses lista de status
     * @param pageable configuração de paginação
     * @return página de pedidos com os status especificados
     */
    Page<OrderEntity> findByStatusIn(List<OrderStatus> statuses, Pageable pageable);
    
    /**
     * Busca os últimos pedidos de um cliente
     * 
     * @param customerId o ID do cliente
     * @param pageable configuração de paginação
     * @return página dos últimos pedidos do cliente
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.customerId = :customerId ORDER BY o.createdAt DESC")
    Page<OrderEntity> findLatestOrdersByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);
    
    /**
     * Busca todos os pedidos ordenados por data de criação (mais recentes primeiro)
     * 
     * @param pageable configuração de paginação
     * @return página de pedidos ordenados por data
     */
    Page<OrderEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Busca pedidos por status e criados antes de uma data específica
     * 
     * @param status o status do pedido
     * @param createdBefore data limite para criação
     * @return lista de pedidos que atendem aos critérios
     */
    List<OrderEntity> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime createdBefore);
    
    /**
     * Calcula o total de vendas de um cliente
     * 
     * @param customerId o ID do cliente
     * @return valor total das vendas do cliente
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM OrderEntity o WHERE o.customerId = :customerId AND o.status IN ('CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED')")
    BigDecimal calculateTotalSalesByCustomerId(@Param("customerId") UUID customerId);
}