package com.techbra.order.infrastructure.persistence;

import com.techbra.order.domain.Order;
import com.techbra.order.domain.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper responsável pela conversão entre entidades de domínio e entidades JPA
 * 
 * Esta classe implementa o padrão Mapper para converter objetos do domínio
 * (Order, OrderItem) para entidades JPA (OrderEntity, OrderItemEntity) e vice-versa.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
@Component
public class OrderMapper {
    
    /**
     * Converte uma entidade de domínio Order para OrderEntity (JPA)
     * 
     * @param order a entidade de domínio
     * @return a entidade JPA correspondente
     */
    public OrderEntity toEntity(Order order) {
        if (order == null) {
            return null;
        }
        
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setOrderNumber(order.getOrderNumber());
        entity.setCustomerId(order.getCustomerId());
        entity.setStatus(order.getStatus());
        // Note: subtotal, taxAmount, shippingAmount não estão na entidade
        entity.setDiscountAmount(order.getDiscountAmount());
        entity.setTotalAmount(order.getTotalAmount());
        // Note: shippingAddress, billingAddress, paymentMethod não estão mapeados na entidade
        entity.setCustomerNotes(order.getCustomerNotes());
        entity.setInternalNotes(order.getInternalNotes());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());
        entity.setVersion(order.getVersion());
        
        // Converter itens
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            List<OrderItemEntity> itemEntities = order.getItems().stream()
                    .map(item -> toItemEntity(item, entity))
                    .collect(Collectors.toList());
            entity.setItems(itemEntities);
        }
        
        return entity;
    }
    
    /**
     * Converte uma OrderEntity (JPA) para entidade de domínio Order
     * 
     * @param entity a entidade JPA
     * @return a entidade de domínio correspondente
     */
    public Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Order order = new Order();
        order.setCustomerId(entity.getCustomerId());
        
        // Definir propriedades que não são definidas no construtor
        order.setId(entity.getId());
        order.setOrderNumber(entity.getOrderNumber());
        order.setStatus(entity.getStatus());
        // Note: subtotal, taxAmount, shippingAmount não estão na entidade
        order.setDiscountAmount(entity.getDiscountAmount());
        order.setTotalAmount(entity.getTotalAmount());
        order.setCustomerNotes(entity.getCustomerNotes());
        order.setInternalNotes(entity.getInternalNotes());
        order.setCreatedAt(entity.getCreatedAt());
        order.setUpdatedAt(entity.getUpdatedAt());
        order.setVersion(entity.getVersion());
        
        // Converter itens
        if (entity.getItems() != null && !entity.getItems().isEmpty()) {
            List<OrderItem> domainItems = entity.getItems().stream()
                    .map(this::toItemDomain)
                    .collect(Collectors.toList());
            order.setItems(domainItems);
        }
        
        return order;
    }
    
    /**
     * Converte uma lista de OrderEntity para lista de Order
     * 
     * @param entities lista de entidades JPA
     * @return lista de entidades de domínio
     */
    public List<Order> toDomainList(List<OrderEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Converte uma lista de Order para lista de OrderEntity
     * 
     * @param orders lista de entidades de domínio
     * @return lista de entidades JPA
     */
    public List<OrderEntity> toEntityList(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }
        
        return orders.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Converte um OrderItem de domínio para OrderItemEntity (JPA)
     * 
     * @param item o item de domínio
     * @param orderEntity a entidade de pedido pai
     * @return a entidade JPA do item
     */
    private OrderItemEntity toItemEntity(OrderItem item, OrderEntity orderEntity) {
        if (item == null) {
            return null;
        }
        
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(item.getId());
        entity.setOrder(orderEntity);
        entity.setProductId(item.getProductId());
        entity.setProductName(item.getProductName());
        entity.setProductSku(item.getProductSku());
        entity.setUnitPrice(item.getUnitPrice());
        entity.setQuantity(item.getQuantity());
        entity.setTotalPrice(item.getTotalPrice());
        entity.setProductDescription(item.getProductDescription());
        entity.setProductImageUrl(item.getProductImageUrl());
        entity.setCreatedAt(item.getCreatedAt());
        entity.setUpdatedAt(item.getUpdatedAt());
        entity.setVersion(item.getVersion());
        
        return entity;
    }
    
    /**
     * Converte um OrderItemEntity (JPA) para OrderItem de domínio
     * 
     * @param entity a entidade JPA do item
     * @return o item de domínio
     */
    private OrderItem toItemDomain(OrderItemEntity entity) {
        if (entity == null) {
            return null;
        }
        
        OrderItem item = new OrderItem(
                entity.getProductId(),
                entity.getProductName(),
                entity.getProductSku(),
                entity.getUnitPrice(),
                entity.getQuantity()
        );
        
        // Definir propriedades que não são definidas no construtor
        item.setId(entity.getId());
        item.setTotalPrice(entity.getTotalPrice());
        item.setProductDescription(entity.getProductDescription());
        item.setProductImageUrl(entity.getProductImageUrl());
        item.setCreatedAt(entity.getCreatedAt());
        item.setUpdatedAt(entity.getUpdatedAt());
        item.setVersion(entity.getVersion());
        
        return item;
    }
    
    /**
     * Atualiza uma OrderEntity existente com dados de uma Order de domínio
     * 
     * @param entity a entidade JPA a ser atualizada
     * @param order a entidade de domínio com os novos dados
     */
    public void updateEntity(OrderEntity entity, Order order) {
        if (entity == null || order == null) {
            return;
        }
        
        entity.setStatus(order.getStatus());
        // Note: subtotal, taxAmount, shippingAmount não estão na entidade
        entity.setDiscountAmount(order.getDiscountAmount());
        entity.setTotalAmount(order.getTotalAmount());
        // Note: shippingAddress, billingAddress, paymentMethod não estão mapeados na entidade
        entity.setCustomerNotes(order.getCustomerNotes());
        entity.setInternalNotes(order.getInternalNotes());
        entity.setUpdatedAt(order.getUpdatedAt());
        
        // Atualizar itens se necessário
        if (order.getItems() != null) {
            // Limpar itens existentes
            entity.getItems().clear();
            
            // Adicionar novos itens
            List<OrderItemEntity> newItems = order.getItems().stream()
                    .map(item -> toItemEntity(item, entity))
                    .collect(Collectors.toList());
            entity.getItems().addAll(newItems);
        }
    }
}