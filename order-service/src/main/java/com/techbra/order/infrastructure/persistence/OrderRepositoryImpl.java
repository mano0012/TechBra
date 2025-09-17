package com.techbra.order.infrastructure.persistence;

import com.techbra.order.domain.Order;
import com.techbra.order.domain.OrderRepository;
import com.techbra.order.domain.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementação do repositório de domínio OrderRepository
 * 
 * Esta classe implementa a interface OrderRepository do domínio,
 * fazendo a ponte entre a camada de domínio e a infraestrutura JPA.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
@Repository
@Transactional
public class OrderRepositoryImpl implements OrderRepository {
    
    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;
    
    @Autowired
    public OrderRepositoryImpl(OrderJpaRepository jpaRepository, OrderMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return jpaRepository.findByOrderNumber(orderNumber)
                .map(mapper::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByCustomerId(UUID customerId) {
        List<OrderEntity> entities = jpaRepository.findByCustomerId(customerId);
        return mapper.toDomainList(entities);
    }
    

    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStatus(OrderStatus status) {
        List<OrderEntity> entities = jpaRepository.findByStatus(status);
        return mapper.toDomainList(entities);
    }
    

    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByCustomerIdAndStatus(UUID customerId, OrderStatus status) {
        List<OrderEntity> entities = jpaRepository.findByCustomerIdAndStatus(customerId, status);
        return mapper.toDomainList(entities);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderEntity> entities = jpaRepository.findByCreatedAtBetween(startDate, endDate);
        return mapper.toDomainList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByCustomerIdAndCreatedAtBetween(UUID customerId, LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderEntity> entities = jpaRepository.findByCustomerIdAndCreatedAtBetween(customerId, startDate, endDate);
        return mapper.toDomainList(entities);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> entityPage = jpaRepository.findAll(pageable);
        return mapper.toDomainList(entityPage.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByCustomerId(UUID customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> entityPage = jpaRepository.findByCustomerId(customerId, pageable);
        return mapper.toDomainList(entityPage.getContent());
    }
    
    @Override
    public void delete(Order order) {
        if (order != null && order.getId() != null) {
            jpaRepository.deleteById(order.getId());
        }
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByOrderNumber(String orderNumber) {
        return jpaRepository.existsByOrderNumber(orderNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findPendingOrdersOlderThan(LocalDateTime olderThan) {
        List<OrderEntity> entities = jpaRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, olderThan);
        return mapper.toDomainList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStatusIn(List<OrderStatus> statuses) {
        List<OrderEntity> entities = jpaRepository.findByStatusIn(statuses);
        return mapper.toDomainList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findLatestByCustomerId(UUID customerId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<OrderEntity> entities = jpaRepository.findByCustomerId(customerId);
        return entities.stream()
            .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
            .limit(limit)
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByCustomerId(UUID customerId) {
        return jpaRepository.countByCustomerId(customerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByStatus(OrderStatus status) {
        return jpaRepository.countByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return jpaRepository.count();
    }
    

    
    /**
     * Atualiza uma ordem existente
     * 
     * @param order a ordem com os dados atualizados
     * @return a ordem atualizada
     */
    public Order update(Order order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Order and Order ID cannot be null for update operation");
        }
        
        Optional<OrderEntity> existingEntity = jpaRepository.findById(order.getId());
        if (existingEntity.isPresent()) {
            OrderEntity entity = existingEntity.get();
            mapper.updateEntity(entity, order);
            OrderEntity updatedEntity = jpaRepository.save(entity);
            return mapper.toDomain(updatedEntity);
        } else {
            throw new IllegalArgumentException("Order with ID " + order.getId() + " not found");
        }
    }
}