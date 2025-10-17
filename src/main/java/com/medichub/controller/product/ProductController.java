package com.medichub.controller.product;

import com.medichub.model.Product;
import com.medichub.service.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    // Shows the details of a product on the product-details page
    @GetMapping("/product-details/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {

        log.info("Called product details for product with ID: " + id);

        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product/product-details";
    }
}
