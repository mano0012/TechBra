package com.techbra.bff.web.controller;

import com.techbra.bff.application.dto.ProductResponse;
import com.techbra.bff.domain.port.in.ProductUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
// TODO: Adicionar a URL do front que vai ter permissão para chamar
// @CrossOrigin(origins = {"http://localhost:8080", "https://app.cliente.com"})
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productUseCase.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {
        ProductResponse product = productUseCase.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        List<ProductResponse> products = productUseCase.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String query) {
        List<ProductResponse> products = productUseCase.searchProducts(query);
        return ResponseEntity.ok(products);
    }
}