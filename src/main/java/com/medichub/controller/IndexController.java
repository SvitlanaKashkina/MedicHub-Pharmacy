package com.medichub.controller;

import com.medichub.controller.cart.CartController;
import com.medichub.service.product.ProductService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class IndexController {

    private final ProductService productService;

    public IndexController(ProductService productService) {
        this.productService = productService;
    }

    private static final Logger log =  LoggerFactory.getLogger(IndexController.class);

    // Shows all products from the db on the index page
    @GetMapping("/")
    public String index(Model model) {

        log.info("Called index page");

        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }
}
