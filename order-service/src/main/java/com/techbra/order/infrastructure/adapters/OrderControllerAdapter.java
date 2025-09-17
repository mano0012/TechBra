package com.techbra.order.infrastructure.adapters;

import com.techbra.order.domain.Order;
import com.techbra.order.domain.OrderStatus;
import com.techbra.order.domain.ports.in.OrderUseCase;
import com.techbra.order.web.dto.CreateOrderRequest;
import com.techbra.order.web.dto.OrderItemRequest;
import com.techbra.order.web.dto.OrderResponse;
import com.techbra.order.web.dto.AddItemRequest;
import com.techbra.order.web.dto.UpdateQuantityRequest;
import com.techbra.order.web.dto.ApplyDiscountRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador de entrada que expõe as funcionalidades do domínio via REST API
 * 
 * Este controlador adapta as requisições HTTP para chamadas aos casos de uso
 * do domínio, seguindo os princípios da arquitetura hexagonal.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/orders")
public class OrderControllerAdapter {
    
    private final OrderUseCase orderUseCase;
    
    public OrderControllerAdapter(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }
    
    /**
     * Cria um novo pedido
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderUseCase.createOrder(
            request.getCustomerId(),
            request.getShippingAddress(),
            request.getBillingAddress(),
            request.getPaymentMethod()
        );
        
        // Adicionar itens se existirem
        if (request.hasItems()) {
            for (OrderItemRequest itemRequest : request.getItems()) {
                order = orderUseCase.addItemToOrder(
                    order.getId(),
                    itemRequest.getProductId(),
                    itemRequest.getProductName(),
                    itemRequest.getProductSku(),
                    itemRequest.getUnitPrice(),
                    itemRequest.getQuantity()
                );
            }
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderResponse.fromDomain(order));
    }
    
    /**
     * Busca todos os pedidos com paginação
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Order> orders = orderUseCase.findAllOrders(page, size);
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Adiciona item ao pedido
     */
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponse> addItemToOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody AddItemRequest request) {
        Order order = orderUseCase.addItemToOrder(
            orderId,
            request.getProductId(),
            request.getProductName(),
            request.getProductSku(),
            request.getUnitPrice(),
            request.getQuantity()
        );
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }
    
    /**
     * Remove item do pedido
     */
    @DeleteMapping("/{orderId}/items/{productId}")
    public ResponseEntity<OrderResponse> removeItemFromOrder(
            @PathVariable UUID orderId,
            @PathVariable UUID productId) {
        Order order = orderUseCase.removeItemFromOrder(orderId, productId);
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }
    
    /**
     * Atualiza quantidade de um item
     */
    @PutMapping("/{orderId}/items/{productId}/quantity")
    public ResponseEntity<OrderResponse> updateItemQuantity(
            @PathVariable UUID orderId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        Order order = orderUseCase.updateItemQuantity(
            orderId,
            productId,
            request.getQuantity()
        );
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }
    
    /**
     * Confirma o pedido
     */
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable UUID orderId) {
        Order order = orderUseCase.confirmOrder(orderId);
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }
    
    /**
     * Cancela o pedido
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID orderId) {
        Order order = orderUseCase.cancelOrder(orderId, "Cancelado pelo usuário");
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }
    
    /**
     * Aplica desconto ao pedido
     */
    @PostMapping("/{orderId}/discount")
    public ResponseEntity<OrderResponse> applyDiscount(
            @PathVariable UUID orderId,
            @Valid @RequestBody ApplyDiscountRequest request) {
        Order order = orderUseCase.applyDiscount(
            orderId,
            request.getDiscountAmount(),
            request.getDiscountReason()
        );
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }
    
    /**
     * Busca pedido por ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId) {
        Optional<Order> orderOpt = orderUseCase.findOrderById(orderId);
        if (orderOpt.isPresent()) {
            return ResponseEntity.ok(OrderResponse.fromDomain(orderOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Busca pedido por número
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
        Optional<Order> orderOpt = orderUseCase.findOrderByNumber(orderNumber);
        if (orderOpt.isPresent()) {
            return ResponseEntity.ok(OrderResponse.fromDomain(orderOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Busca pedidos de um cliente
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Order> orders = orderUseCase.findOrdersByCustomerIdPaginated(customerId, page, size);
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca pedidos por status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderUseCase.findOrdersByStatus(status);
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca pedidos ativos de um cliente
     */
    @GetMapping("/customer/{customerId}/active")
    public ResponseEntity<List<OrderResponse>> getActiveOrdersByCustomerId(@PathVariable UUID customerId) {
        List<Order> orders = orderUseCase.findActiveOrdersByCustomerId(customerId);
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca pedidos canceláveis de um cliente
     */
    @GetMapping("/customer/{customerId}/cancellable")
    public ResponseEntity<List<OrderResponse>> getCancellableOrdersByCustomerId(@PathVariable UUID customerId) {
        List<Order> orders = orderUseCase.findCancellableOrdersByCustomerId(customerId);
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Conta pedidos de um cliente
     */
    @GetMapping("/customer/{customerId}/count")
    public ResponseEntity<Long> countOrdersByCustomerId(@PathVariable UUID customerId) {
        Long count = orderUseCase.countOrdersByCustomerId(customerId);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Conta pedidos por status
     */
    @GetMapping("/status/{status}/count")
    public ResponseEntity<Long> countOrdersByStatus(@PathVariable OrderStatus status) {
        Long count = orderUseCase.countOrdersByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Calcula total de vendas de um cliente
     */
    @GetMapping("/customer/{customerId}/total-sales")
    public ResponseEntity<BigDecimal> getCustomerTotalSales(@PathVariable UUID customerId) {
        BigDecimal totalSales = orderUseCase.calculateCustomerTotalSales(customerId);
        return ResponseEntity.ok(totalSales);
    }
    
    /**
     * Busca pedidos por período
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<OrderResponse>> getOrdersByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<Order> orders = orderUseCase.findOrdersByDateRange(startDate, endDate);
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca pedidos de um cliente por status
     */
    @GetMapping("/customer/{customerId}/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerIdAndStatus(
            @PathVariable UUID customerId,
            @PathVariable OrderStatus status) {
        List<Order> orders = orderUseCase.findOrdersByCustomerIdAndStatus(customerId, status);
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}