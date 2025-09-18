package com.techbra.logistics.domain.ports.out;

import com.techbra.logistics.domain.model.Shipment;
import com.techbra.logistics.domain.model.ShipmentStatus;

import java.util.List;
import java.util.Optional;

public interface ShipmentRepositoryPort {
    
    /**
     * Salva um envio
     * @param shipment envio a ser salvo
     * @return envio salvo com ID gerado
     */
    Shipment save(Shipment shipment);
    
    /**
     * Busca um envio pelo ID
     * @param id ID do envio
     * @return envio encontrado ou vazio
     */
    Optional<Shipment> findById(Long id);
    
    /**
     * Busca um envio pelo ID do pedido
     * @param orderId ID do pedido
     * @return envio encontrado ou vazio
     */
    Optional<Shipment> findByOrderId(Long orderId);
    
    /**
     * Busca envios pelo ID do pedido (pode haver múltiplos)
     * @param orderId ID do pedido
     * @return lista de envios do pedido
     */
    List<Shipment> findAllByOrderId(Long orderId);
    
    /**
     * Lista todos os envios
     * @return lista de todos os envios
     */
    List<Shipment> findAll();
    
    /**
     * Lista envios por status
     * @param status status do envio
     * @return lista de envios com o status especificado
     */
    List<Shipment> findByStatus(ShipmentStatus status);
    
    /**
     * Lista envios por email do cliente
     * @param customerEmail email do cliente
     * @return lista de envios do cliente
     */
    List<Shipment> findByCustomerEmail(String customerEmail);
    
    /**
     * Verifica se existe um envio para o pedido especificado
     * @param orderId ID do pedido
     * @return true se existe, false caso contrário
     */
    boolean existsByOrderId(Long orderId);
    
    /**
     * Remove um envio pelo ID
     * @param id ID do envio
     */
    void deleteById(Long id);
    
    /**
     * Conta o número total de envios
     * @return número total de envios
     */
    long count();
    
    /**
     * Conta envios por status
     * @param status status do envio
     * @return número de envios com o status especificado
     */
    long countByStatus(ShipmentStatus status);
}