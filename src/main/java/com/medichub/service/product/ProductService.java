package com.medichub.service.product;

import com.medichub.model.Product;
import com.medichub.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Shows all products from the db on the index page
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Shows the details of a product on the product-details page
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
    }
}
