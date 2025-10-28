package com.medichub.service.product;

import com.medichub.controller.admin.AdminController;
import com.medichub.model.Product;
import com.medichub.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    // Shows all products from the db on the index page
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Shows the details of a product on the product-details page
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
    }

    public List<Product> findByNameOrCategory(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }
    //  Add product (admin)
    public Product addProduct(Product product) {
        log.info("Adding product with name {}", product.getName());
        product.setCreatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    // Update product (admin)
    public void updateProduct(Long id, String name, String description, String category, double price, int stock) {
        log.info("Updating product with ID {}", id);

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product with ID " + id + " not found"));

        existing.setName(name);
        existing.setDescription(description);
        existing.setCategory(category);
        existing.setPrice(price);
        existing.setStock(stock);

        productRepository.save(existing);
    }

    // delete product (admin)
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot delete product. ID " + id + " does not exist."));
        productRepository.delete(product);
    }
}
