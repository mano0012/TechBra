package com.techbra.bff.domain.port.out;

import com.techbra.bff.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductPort {
    List<Product> findAll();
    Optional<Product> findById(String id);
    List<Product> findByCategory(String category);
    List<Product> searchByName(String name);
}