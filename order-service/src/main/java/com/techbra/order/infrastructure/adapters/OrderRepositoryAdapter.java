package com.techbra.order.infrastructure.adapters;

import com.techbra.order.domain.Order;
import com.techbra.order.domain.OrderStatus;
import com.techbra.order.domain.ports.out.OrderRepositoryPort;
import com.techbra.order.infrastructure.persistence.OrderJpaRepository;
import com.techbra.order.infrastructure.persistence.OrderMapper;
import com.techbra.order.infrastructure.persistence.OrderEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa a porta de saída OrderRepositoryPort
 * 
 * Esta classe adapta as operações de persistência JPA para a interface
 * de domínio, seguindo os princípios da arquitetura hexagonal.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public class OrderRepositoryAdapter implements OrderRepositoryPort {
    
    private final OrderJpaRepository orderJpaRepository;
    private final OrderMapper orderMapper;
    
    public OrderRepositoryAdapter(OrderJpaRepository orderJpaRepository, OrderMapper orderMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderMapper = orderMapper;
    }
    
    @Override
    public Order save(Order order) {
        var orderEntity = orderMapper.toEntity(order);
        var savedEntity = orderJpaRepository.save(orderEntity);
        return orderMapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Order> findById(UUID id) {
        return orderJpaRepository.findById(id)
                .map(orderMapper::toDomain);
    }
    
    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderJpaRepository.findByOrderNumber(orderNumber)
                .map(orderMapper::toDomain);
    }
    
    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        return orderJpaRepository.findByCustomerId(customerId)
                .stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orderJpaRepository.findByStatus(status)
                .stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByCustomerIdAndStatus(UUID customerId, OrderStatus status) {
        return orderJpaRepository.findByCustomerIdAndStatus(customerId, status)
                .stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return orderJpaRepository.findByCreatedAtBetween(startDate, endDate)
                .stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByCustomerIdAndCreatedAtBetween(UUID customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderJpaRepository.findByCustomerIdAndCreatedAtBetween(customerId, startDate, endDate)
                .stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public BigDecimal calculateTotalSalesByCustomerId(UUID customerId) {
        return orderJpaRepository.calculateTotalSalesByCustomerId(customerId);
    }
    
    @Override
    public Long countByCustomerId(UUID customerId) {
        return orderJpaRepository.countByCustomerId(customerId);
    }
    
    @Override
    public Long countByStatus(OrderStatus status) {
        return orderJpaRepository.countByStatus(status);
    }
    
    @Override
    public boolean existsByOrderNumber(String orderNumber) {
        return orderJpaRepository.existsByOrderNumber(orderNumber);
    }
    
    @Override
    public void deleteById(UUID id) {
        orderJpaRepository.deleteById(id);
    }
    
    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll()
                .stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findAllPaginated(int page, int size) {
        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return orderJpaRepository.findAll(pageable)
                .stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByCustomerIdPaginated(UUID customerId, int page, int size) {
        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return orderJpaRepository.findByCustomerId(customerId, pageable)
                .stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findOrdersByCustomerIdPaginated(UUID customerId, int page, int size) {
        return findByCustomerIdPaginated(customerId, page, size);
    }
    
    @Override
    public List<Order> findActiveOrdersByCustomerId(UUID customerId) {
        List<OrderEntity> pendingOrders = orderJpaRepository.findByCustomerIdAndStatus(customerId, OrderStatus.PENDING);
        List<OrderEntity> confirmedOrders = orderJpaRepository.findByCustomerIdAndStatus(customerId, OrderStatus.CONFIRMED);
        List<OrderEntity> preparingOrders = orderJpaRepository.findByCustomerIdAndStatus(customerId, OrderStatus.PREPARING);
        
        List<OrderEntity> allActiveOrders = new ArrayList<>();
        allActiveOrders.addAll(pendingOrders);
        allActiveOrders.addAll(confirmedOrders);
        allActiveOrders.addAll(preparingOrders);
        
        return allActiveOrders.stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findCancellableOrdersByCustomerId(UUID customerId) {
        List<OrderEntity> pendingOrders = orderJpaRepository.findByCustomerIdAndStatus(customerId, OrderStatus.PENDING);
        List<OrderEntity> confirmedOrders = orderJpaRepository.findByCustomerIdAndStatus(customerId, OrderStatus.CONFIRMED);
        
        List<OrderEntity> allCancellableOrders = new ArrayList<>();
        allCancellableOrders.addAll(pendingOrders);
        allCancellableOrders.addAll(confirmedOrders);
        
        return allCancellableOrders.stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Long countOrdersByStatus(OrderStatus status) {
        return countByStatus(status);
    }
}