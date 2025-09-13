package com.techbra.bff.domain.port.in;

import com.techbra.bff.application.dto.ProductResponse;
import java.util.List;

public interface ProductUseCase {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(String id);
    List<ProductResponse> getProductsByCategory(String category);
    List<ProductResponse> searchProducts(String query);
}