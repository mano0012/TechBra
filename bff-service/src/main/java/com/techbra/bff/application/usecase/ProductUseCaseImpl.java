package com.techbra.bff.application.usecase;

import com.techbra.bff.application.dto.ProductResponse;
import com.techbra.bff.domain.model.Product;
import com.techbra.bff.domain.port.in.ProductUseCase;
import com.techbra.bff.domain.port.out.ProductPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductUseCaseImpl implements ProductUseCase {
    
    private final ProductPort productPort;
    
    public ProductUseCaseImpl(ProductPort productPort) {
        this.productPort = productPort;
    }
    
    @Override
    public List<ProductResponse> getAllProducts() {
        return productPort.findAll()
            .stream()
            .map(this::toProductResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public ProductResponse getProductById(String id) {
        return productPort.findById(id)
            .map(this::toProductResponse)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
    }
    
    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        return productPort.findByCategory(category)
            .stream()
            .map(this::toProductResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductResponse> searchProducts(String query) {
        return productPort.searchByName(query)
            .stream()
            .map(this::toProductResponse)
            .collect(Collectors.toList());
    }
    
    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategory(),
            product.isAvailable()
        );
    }
}