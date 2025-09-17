package com.techbra.order.domain.ports.in;

import com.techbra.order.domain.Order;
import com.techbra.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface que define os casos de uso para gerenciamento de pedidos
 * 
 * Esta interface representa a porta de entrada (inbound port) da arquitetura hexagonal,
 * definindo as operações de negócio disponíveis para o domínio de pedidos.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public interface OrderUseCase {
    
    /**
     * Cria um novo pedido
     * 
     * @param customerId ID do cliente
     * @param shippingAddress endereço de entrega
     * @param billingAddress endereço de cobrança
     * @param paymentMethod método de pagamento
     * @return o pedido criado
     */
    Order createOrder(UUID customerId, String shippingAddress, 
                     String billingAddress, String paymentMethod);
    
    /**
     * Adiciona um item ao pedido
     * 
     * @param orderId ID do pedido
     * @param productId ID do produto
     * @param productName nome do produto
     * @param productSku SKU do produto
     * @param unitPrice preço unitário
     * @param quantity quantidade
     * @return o pedido atualizado
     */
    Order addItemToOrder(UUID orderId, UUID productId, String productName, 
                        String productSku, BigDecimal unitPrice, Integer quantity);
    
    /**
     * Remove um item do pedido
     * 
     * @param orderId ID do pedido
     * @param itemId ID do item
     * @return o pedido atualizado
     */
    Order removeItemFromOrder(UUID orderId, UUID itemId);
    
    /**
     * Atualiza a quantidade de um item no pedido
     * 
     * @param orderId ID do pedido
     * @param itemId ID do item
     * @param newQuantity nova quantidade
     * @return o pedido atualizado
     */
    Order updateItemQuantity(UUID orderId, UUID itemId, Integer newQuantity);
    
    /**
     * Confirma um pedido
     * 
     * @param orderId ID do pedido
     * @return o pedido confirmado
     */
    Order confirmOrder(UUID orderId);
    
    /**
     * Cancela um pedido
     * 
     * @param orderId ID do pedido
     * @param reason motivo do cancelamento
     * @return o pedido cancelado
     */
    Order cancelOrder(UUID orderId, String reason);
    
    /**
     * Aplica desconto ao pedido
     * 
     * @param orderId ID do pedido
     * @param discountAmount valor do desconto
     * @param discountReason motivo do desconto
     * @return o pedido com desconto aplicado
     */
    Order applyDiscount(UUID orderId, BigDecimal discountAmount, String discountReason);
    
    /**
     * Busca um pedido por ID
     * 
     * @param orderId ID do pedido
     * @return Optional contendo o pedido se encontrado
     */
    Optional<Order> findOrderById(UUID orderId);
    
    /**
     * Busca um pedido por número
     * 
     * @param orderNumber número do pedido
     * @return Optional contendo o pedido se encontrado
     */
    Optional<Order> findOrderByNumber(String orderNumber);
    
    /**
     * Busca todos os pedidos de um cliente
     * 
     * @param customerId ID do cliente
     * @return lista de pedidos do cliente
     */
    List<Order> findOrdersByCustomerId(UUID customerId);
    
    /**
     * Busca pedidos por status
     * 
     * @param status status dos pedidos
     * @return lista de pedidos com o status especificado
     */
    List<Order> findOrdersByStatus(OrderStatus status);
    
    /**
     * Busca pedidos de um cliente por status
     * 
     * @param customerId ID do cliente
     * @param status status dos pedidos
     * @return lista de pedidos do cliente com o status especificado
     */
    List<Order> findOrdersByCustomerIdAndStatus(UUID customerId, OrderStatus status);
    
    /**
     * Busca pedidos criados em um período
     * 
     * @param startDate data inicial
     * @param endDate data final
     * @return lista de pedidos criados no período
     */
    List<Order> findOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Calcula o total de vendas de um cliente
     * 
     * @param customerId ID do cliente
     * @return valor total das vendas
     */
    BigDecimal calculateCustomerTotalSales(UUID customerId);
    
    /**
     * Conta o número de pedidos de um cliente
     * 
     * @param customerId ID do cliente
     * @return número de pedidos
     */
    Long countOrdersByCustomerId(UUID customerId);
    
    /**
     * Busca pedidos de um cliente com paginação
     * 
     * @param customerId ID do cliente
     * @param page número da página
     * @param size tamanho da página
     * @return lista paginada de pedidos do cliente
     */
    List<Order> findOrdersByCustomerIdPaginated(UUID customerId, int page, int size);
    
    /**
     * Busca pedidos ativos de um cliente
     * 
     * @param customerId ID do cliente
     * @return lista de pedidos ativos do cliente
     */
    List<Order> findActiveOrdersByCustomerId(UUID customerId);
    
    /**
     * Busca pedidos canceláveis de um cliente
     * 
     * @param customerId ID do cliente
     * @return lista de pedidos canceláveis do cliente
     */
    List<Order> findCancellableOrdersByCustomerId(UUID customerId);
    
    /**
     * Conta o número de pedidos por status
     * 
     * @param status status dos pedidos
     * @return número de pedidos com o status especificado
     */
    Long countOrdersByStatus(OrderStatus status);
    
    /**
     * Busca todos os pedidos com paginação
     * 
     * @param page número da página
     * @param size tamanho da página
     * @return lista paginada de todos os pedidos
     */
    List<Order> findAllOrders(int page, int size);
}