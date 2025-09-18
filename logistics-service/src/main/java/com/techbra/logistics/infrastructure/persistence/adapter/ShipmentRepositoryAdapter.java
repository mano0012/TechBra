package com.techbra.logistics.infrastructure.persistence.adapter;

import com.techbra.logistics.domain.model.Shipment;
import com.techbra.logistics.domain.model.ShipmentStatus;
import com.techbra.logistics.domain.ports.out.ShipmentRepositoryPort;
import com.techbra.logistics.infrastructure.persistence.entity.ShipmentEntity;
import com.techbra.logistics.infrastructure.persistence.mapper.ShipmentMapper;
import com.techbra.logistics.infrastructure.persistence.repository.ShipmentJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ShipmentRepositoryAdapter implements ShipmentRepositoryPort {
    
    private static final Logger logger = LoggerFactory.getLogger(ShipmentRepositoryAdapter.class);
    
    private final ShipmentJpaRepository jpaRepository;
    private final ShipmentMapper mapper;
    
    public ShipmentRepositoryAdapter(ShipmentJpaRepository jpaRepository, ShipmentMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Shipment save(Shipment shipment) {
        logger.debug("Salvando envio: {}", shipment);
        
        ShipmentEntity entity = mapper.toEntity(shipment);
        ShipmentEntity savedEntity = jpaRepository.save(entity);
        
        Shipment savedShipment = mapper.toDomain(savedEntity);
        logger.debug("Envio salvo com ID: {}", savedShipment.getId());
        
        return savedShipment;
    }
    
    @Override
    public Optional<Shipment> findById(Long id) {
        logger.debug("Buscando envio por ID: {}", id);
        
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Shipment> findByOrderId(Long orderId) {
        logger.debug("Buscando envio por Order ID: {}", orderId);
        
        return jpaRepository.findByOrderId(orderId)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Shipment> findAllByOrderId(Long orderId) {
        logger.debug("Buscando envios por Order ID: {}", orderId);
        
        List<ShipmentEntity> entities = jpaRepository.findAllByOrderId(orderId);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Shipment> findAll() {
        logger.debug("Listando todos os envios");
        
        List<ShipmentEntity> entities = jpaRepository.findAllByOrderByCreatedAtDesc();
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Shipment> findByStatus(ShipmentStatus status) {
        logger.debug("Buscando envios por status: {}", status);
        
        List<ShipmentEntity> entities = jpaRepository.findByStatusOrderByCreatedAtDesc(status);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Shipment> findByCustomerEmail(String customerEmail) {
        logger.debug("Buscando envios por email do cliente: {}", customerEmail);
        
        List<ShipmentEntity> entities = jpaRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public boolean existsByOrderId(Long orderId) {
        logger.debug("Verificando se existe envio para Order ID: {}", orderId);
        
        boolean exists = jpaRepository.existsByOrderId(orderId);
        logger.debug("Envio existe para Order ID {}: {}", orderId, exists);
        
        return exists;
    }
    
    @Override
    public void deleteById(Long id) {
        logger.info("Removendo envio com ID: {}", id);
        
        if (!jpaRepository.existsById(id)) {
            logger.warn("Tentativa de remover envio inexistente: {}", id);
            throw new IllegalArgumentException("Envio não encontrado: " + id);
        }
        
        jpaRepository.deleteById(id);
        logger.info("Envio removido com sucesso: {}", id);
    }
    
    @Override
    public long count() {
        long count = jpaRepository.count();
        logger.debug("Total de envios: {}", count);
        return count;
    }
    
    @Override
    public long countByStatus(ShipmentStatus status) {
        long count = jpaRepository.countByStatus(status);
        logger.debug("Total de envios com status {}: {}", status, count);
        return count;
    }
    
    /**
     * Método adicional para buscar por número de rastreamento
     */
    public Optional<Shipment> findByTrackingNumber(String trackingNumber) {
        logger.debug("Buscando envio por número de rastreamento: {}", trackingNumber);
        
        return jpaRepository.findByTrackingNumber(trackingNumber)
                .map(mapper::toDomain);
    }
    
    /**
     * Método adicional para buscar com filtros múltiplos
     */
    public List<Shipment> findShipmentsWithFilters(ShipmentStatus status, String customerEmail, String city) {
        logger.debug("Buscando envios com filtros - Status: {}, Email: {}, Cidade: {}", 
                    status, customerEmail, city);
        
        List<ShipmentEntity> entities = jpaRepository.findShipmentsWithFilters(status, customerEmail, city);
        return mapper.toDomainList(entities);
    }
}