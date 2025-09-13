package com.techbra.bff.infrastructure.adapter;

import com.techbra.bff.domain.model.Product;
import com.techbra.bff.domain.port.out.ProductPort;
import com.techbra.bff.infrastructure.client.ProductCatalogServiceClient;
import com.techbra.bff.infrastructure.dto.ProductDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductAdapter implements ProductPort {
    
    private final ProductCatalogServiceClient productCatalogServiceClient;
    
    public ProductAdapter(ProductCatalogServiceClient productCatalogServiceClient) {
        this.productCatalogServiceClient = productCatalogServiceClient;
    }
    
    @Override
    public List<Product> findAll() {
        return productCatalogServiceClient.getAllProducts()
            .stream()
            .map(this::toProduct)
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Product> findById(String id) {
        try {
            ProductDto productDto = productCatalogServiceClient.getProduct(id);
            return Optional.of(toProduct(productDto));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Product> findByCategory(String category) {
        return productCatalogServiceClient.getProductsByCategory(category)
            .stream()
            .map(this::toProduct)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> searchByName(String name) {
        return productCatalogServiceClient.searchProducts(name)
            .stream()
            .map(this::toProduct)
            .collect(Collectors.toList());
    }
    
    private Product toProduct(ProductDto dto) {
        return new Product(
            dto.getId(),
            dto.getName(),
            dto.getDescription(),
            dto.getPrice(),
            dto.getCategory(),
            dto.isAvailable()
        );
    }
}