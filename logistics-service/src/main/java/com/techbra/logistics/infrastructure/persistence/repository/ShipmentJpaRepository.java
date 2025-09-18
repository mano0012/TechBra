package com.techbra.logistics.infrastructure.persistence.repository;

import com.techbra.logistics.domain.model.ShipmentStatus;
import com.techbra.logistics.infrastructure.persistence.entity.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentJpaRepository extends JpaRepository<ShipmentEntity, Long> {
    
    /**
     * Busca um envio pelo ID do pedido
     */
    Optional<ShipmentEntity> findByOrderId(Long orderId);
    
    /**
     * Verifica se existe um envio para o pedido especificado
     */
    boolean existsByOrderId(Long orderId);
    
    /**
     * Lista envios por status
     */
    List<ShipmentEntity> findByStatus(ShipmentStatus status);
    
    /**
     * Lista envios por email do cliente
     */
    List<ShipmentEntity> findByCustomerEmail(String customerEmail);
    
    /**
     * Lista envios por email do cliente ordenados por data de criação (mais recentes primeiro)
     */
    List<ShipmentEntity> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);
    
    /**
     * Conta envios por status
     */
    long countByStatus(ShipmentStatus status);
    
    /**
     * Busca envios por cidade
     */
    List<ShipmentEntity> findByCity(String city);
    
    /**
     * Busca envios por estado
     */
    List<ShipmentEntity> findByState(String state);
    
    /**
     * Busca envios por número de rastreamento
     */
    Optional<ShipmentEntity> findByTrackingNumber(String trackingNumber);
    
    /**
     * Lista todos os envios ordenados por data de criação (mais recentes primeiro)
     */
    List<ShipmentEntity> findAllByOrderByCreatedAtDesc();
    
    /**
     * Busca envios por status ordenados por data de criação
     */
    List<ShipmentEntity> findByStatusOrderByCreatedAtDesc(ShipmentStatus status);
    
    /**
     * Query customizada para buscar envios com filtros múltiplos
     */
    @Query("SELECT s FROM ShipmentEntity s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:customerEmail IS NULL OR s.customerEmail = :customerEmail) AND " +
           "(:city IS NULL OR s.city = :city) " +
           "ORDER BY s.createdAt DESC")
    List<ShipmentEntity> findShipmentsWithFilters(
        @Param("status") ShipmentStatus status,
        @Param("customerEmail") String customerEmail,
        @Param("city") String city
    );
}