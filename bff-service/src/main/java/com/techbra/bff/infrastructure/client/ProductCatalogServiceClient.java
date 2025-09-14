package com.techbra.bff.infrastructure.client;

import com.techbra.bff.infrastructure.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-catalog-service", url = "${services.product-catalog.url}")
public interface ProductCatalogServiceClient {
    
    @GetMapping("/api/products")
    List<ProductDto> getAllProducts();
    
    @GetMapping("/api/products/{id}")
    ProductDto getProduct(@PathVariable String id);
    
    @GetMapping("/api/products/category/{category}")
    List<ProductDto> getProductsByCategory(@PathVariable String category);
    
    @GetMapping("/api/products/search")
    List<ProductDto> searchProducts(@RequestParam String query);
}