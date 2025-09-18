package com.techbra.order.domain.service;

import com.techbra.order.domain.Order;
import com.techbra.order.domain.OrderItem;
import com.techbra.order.domain.OrderStatus;
import com.techbra.order.domain.ports.in.OrderUseCase;
import com.techbra.order.domain.ports.out.OrderRepositoryPort;
import com.techbra.order.service.OrderEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço de domínio para gerenciamento de pedidos
 * 
 * Esta classe contém a lógica de negócio relacionada aos pedidos,
 * implementando as regras de negócio e coordenando as operações
 * entre as entidades de domínio seguindo os princípios da arquitetura hexagonal.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
@Service
public class OrderService implements OrderUseCase {
    
    private final OrderRepositoryPort orderRepositoryPort;
    private final OrderEventPublisher orderEventPublisher;

    public OrderService(OrderRepositoryPort orderRepositoryPort, OrderEventPublisher orderEventPublisher) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.orderEventPublisher = orderEventPublisher;
    }
    
    /**
     * Cria um novo pedido
     * 
     * @param customerId ID do cliente
     * @param shippingAddress endereço de entrega
     * @param billingAddress endereço de cobrança
     * @param paymentMethod método de pagamento
     * @return o pedido criado
     */
    @Transactional
    public Order createOrder(UUID customerId, String shippingAddress, 
                           String billingAddress, String paymentMethod) {
        
        validateCustomerId(customerId);
        validateAddresses(shippingAddress, billingAddress);
        validatePaymentMethod(paymentMethod);
        
        Order order = new Order(customerId, null);
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);
        order.setPaymentMethod(paymentMethod);
        
        // Salvar o pedido no banco de dados
        Order savedOrder = orderRepositoryPort.save(order);
        
        // Publicar evento de criação do pedido no Kafka
        try {
            orderEventPublisher.publishOrderCreatedEvent(savedOrder);
        } catch (Exception e) {
            // Log do erro, mas não falha a transação principal
            // Em um cenário real, poderia implementar retry ou dead letter queue
            System.err.println("Erro ao publicar evento de criação do pedido: " + e.getMessage());
        }
        
        return savedOrder;
    }
    
    /**
     * Aplica desconto ao pedido com motivo
     * 
     * @param orderId ID do pedido
     * @param discountAmount valor do desconto
     * @param discountReason motivo do desconto
     * @return o pedido com desconto aplicado
     */
    public Order applyDiscount(UUID orderId, BigDecimal discountAmount, String discountReason) {
        Order order = getOrderById(orderId);
        
        if (!order.canBeModified()) {
            throw new IllegalStateException("Pedido não pode ser modificado no status atual: " + order.getStatus());
        }
        
        order.applyDiscount(discountAmount);
        if (discountReason != null && !discountReason.trim().isEmpty()) {
            order.setNotes(order.getNotes() != null ? 
                order.getNotes() + "\nDesconto aplicado: " + discountReason : 
                "Desconto aplicado: " + discountReason);
        }
        
        return orderRepositoryPort.save(order);
    }
    
    /**
     * Busca pedidos de um cliente por status
     * 
     * @param customerId ID do cliente
     * @param status status dos pedidos
     * @return lista de pedidos do cliente com o status especificado
     */
    public List<Order> findOrdersByCustomerIdAndStatus(UUID customerId, OrderStatus status) {
        return orderRepositoryPort.findByCustomerIdAndStatus(customerId, status);
    }
    
    /**
     * Busca pedidos criados em um período
     * 
     * @param startDate data inicial
     * @param endDate data final
     * @return lista de pedidos criados no período
     */
    public List<Order> findOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepositoryPort.findByCreatedAtBetween(startDate, endDate);
    }
    
    /**
     * Calcula o total de vendas de um cliente
     * 
     * @param customerId ID do cliente
     * @return valor total das vendas
     */
    public BigDecimal calculateCustomerTotalSales(UUID customerId) {
        return orderRepositoryPort.calculateTotalSalesByCustomerId(customerId);
    }
    
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
    public Order addItemToOrder(UUID orderId, UUID productId, String productName, 
                               String productSku, BigDecimal unitPrice, Integer quantity) {
        
        Order order = getOrderById(orderId);
        
        if (!order.canBeModified()) {
            throw new IllegalStateException("Pedido não pode ser modificado no status atual: " + order.getStatus());
        }
        
        validateProductData(productId, productName, unitPrice, quantity);
        
        OrderItem item = new OrderItem(productId, productName, productSku, unitPrice, quantity);
        order.addItem(item);
        
        return orderRepositoryPort.save(order);
    }
    
    /**
     * Remove um item do pedido
     * 
     * @param orderId ID do pedido
     * @param itemId ID do item
     * @return o pedido atualizado
     */
    public Order removeItemFromOrder(UUID orderId, UUID itemId) {
        Order order = getOrderById(orderId);
        
        if (!order.canBeModified()) {
            throw new IllegalStateException("Pedido não pode ser modificado no status atual: " + order.getStatus());
        }
        
        OrderItem itemToRemove = order.getItems().stream()
            .filter(item -> item.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item não encontrado: " + itemId));
        order.removeItem(itemToRemove);
        return orderRepositoryPort.save(order);
    }
    
    /**
     * Atualiza a quantidade de um item no pedido
     * 
     * @param orderId ID do pedido
     * @param itemId ID do item
     * @param newQuantity nova quantidade
     * @return o pedido atualizado
     */
    public Order updateItemQuantity(UUID orderId, UUID itemId, Integer newQuantity) {
        Order order = getOrderById(orderId);
        
        if (!order.canBeModified()) {
            throw new IllegalStateException("Pedido não pode ser modificado no status atual: " + order.getStatus());
        }
        
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        
        Optional<OrderItem> itemOpt = order.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
        
        if (itemOpt.isPresent()) {
            OrderItem item = itemOpt.get();
            item.updateQuantity(newQuantity);
            order.recalculateAmounts();
        } else {
            throw new IllegalArgumentException("Item não encontrado no pedido");
        }
        
        return orderRepositoryPort.save(order);
    }
    
    /**
     * Confirma um pedido
     * 
     * @param orderId ID do pedido
     * @return o pedido confirmado
     */
    public Order confirmOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        
        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Não é possível confirmar um pedido sem itens");
        }
        
        order.confirm();
        return orderRepositoryPort.save(order);
    }
    
    /**
     * Cancela um pedido
     * 
     * @param orderId ID do pedido
     * @param reason motivo do cancelamento
     * @return o pedido cancelado
     */
    public Order cancelOrder(UUID orderId, String reason) {
        Order order = getOrderById(orderId);
        
        if (!order.canBeCancelled()) {
            throw new IllegalStateException("Pedido não pode ser cancelado no status atual: " + order.getStatus());
        }
        
        order.cancel(reason);
        if (reason != null && !reason.trim().isEmpty()) {
            order.setNotes(order.getNotes() != null ? 
                order.getNotes() + "\nMotivo do cancelamento: " + reason : 
                "Motivo do cancelamento: " + reason);
        }
        
        return orderRepositoryPort.save(order);
    }
    
    /**
     * Atualiza o status de um pedido
     * 
     * @param orderId ID do pedido
     * @param newStatus novo status
     * @return o pedido atualizado
     */
    public Order updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        order.updateStatus(newStatus);
        return orderRepositoryPort.save(order);
    }
    
    /**
     * Aplica desconto ao pedido
     * 
     * @param orderId ID do pedido
     * @param discountAmount valor do desconto
     * @return o pedido atualizado
     */
    public Order applyDiscount(UUID orderId, BigDecimal discountAmount) {
        Order order = getOrderById(orderId);
        
        if (!order.canBeModified()) {
            throw new IllegalStateException("Pedido não pode ser modificado no status atual: " + order.getStatus());
        }
        
        order.applyDiscount(discountAmount);
        return orderRepositoryPort.save(order);
    }
    
    /**
     * Busca um pedido por ID
     * 
     * @param orderId ID do pedido
     * @return o pedido encontrado
     */
    @Override
    public Optional<Order> findOrderById(UUID orderId) {
        return orderRepositoryPort.findById(orderId);
    }
    
    /**
     * Busca um pedido por ID (método auxiliar interno)
     * 
     * @param orderId ID do pedido
     * @return o pedido encontrado
     * @throws IllegalArgumentException se o pedido não for encontrado
     */
    private Order getOrderById(UUID orderId) {
        return orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com ID: " + orderId));
    }
    
    /**
     * Busca um pedido por número
     * 
     * @param orderNumber número do pedido
     * @return o pedido encontrado
     */
    public Optional<Order> findOrderByNumber(String orderNumber) {
        return orderRepositoryPort.findByOrderNumber(orderNumber);
    }
    
    /**
     * Busca pedidos de um cliente
     * 
     * @param customerId ID do cliente
     * @return lista de pedidos do cliente
     */
    public List<Order> findOrdersByCustomerId(UUID customerId) {
        return orderRepositoryPort.findByCustomerId(customerId);
    }
    
    /**
     * Busca pedidos por status
     * 
     * @param status status do pedido
     * @return lista de pedidos com o status especificado
     */
    public List<Order> findOrdersByStatus(OrderStatus status) {
        return orderRepositoryPort.findByStatus(status);
    }
    
    /**
     * Conta o número de pedidos de um cliente
     * 
     * @param customerId ID do cliente
     * @return número de pedidos
     */
    public Long countOrdersByCustomerId(UUID customerId) {
        return orderRepositoryPort.countByCustomerId(customerId);
    }
    
    // Métodos de validação privados
    
    private void validateCustomerId(UUID customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("ID do cliente é obrigatório");
        }
    }
    
    private void validateAddresses(String shippingAddress, String billingAddress) {
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereço de entrega é obrigatório");
        }
        if (billingAddress == null || billingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereço de cobrança é obrigatório");
        }
    }
    
    private void validatePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Método de pagamento é obrigatório");
        }
    }
    
    private void validateProductData(UUID productId, String productName, 
                                   BigDecimal unitPrice, Integer quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("ID do produto é obrigatório");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço unitário deve ser maior que zero");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
    }
    
    @Override
    public List<Order> findOrdersByCustomerIdPaginated(UUID customerId, int page, int size) {
        if (customerId == null) {
            throw new IllegalArgumentException("ID do cliente é obrigatório");
        }
        return orderRepositoryPort.findOrdersByCustomerIdPaginated(customerId, page, size);
    }
    
    @Override
    public List<Order> findActiveOrdersByCustomerId(UUID customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("ID do cliente é obrigatório");
        }
        return orderRepositoryPort.findActiveOrdersByCustomerId(customerId);
    }
    
    @Override
    public List<Order> findCancellableOrdersByCustomerId(UUID customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("ID do cliente é obrigatório");
        }
        return orderRepositoryPort.findCancellableOrdersByCustomerId(customerId);
    }
    
    @Override
    public Long countOrdersByStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório");
        }
        return orderRepositoryPort.countOrdersByStatus(status);
    }
    
    @Override
    public List<Order> findAllOrders(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Número da página deve ser maior ou igual a 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Tamanho da página deve ser maior que 0");
        }
        return orderRepositoryPort.findAllPaginated(page, size);
    }
}