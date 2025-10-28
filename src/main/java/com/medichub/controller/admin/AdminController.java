package com.medichub.controller.admin;

import com.medichub.controller.auth.AuthController;
import com.medichub.model.Product;
import com.medichub.model.User;
import com.medichub.repository.OrderRepository;
import com.medichub.repository.ProductRepository;
import com.medichub.repository.UserRepository;
import com.medichub.service.product.ProductService;
import com.medichub.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long paidOrders = orderRepository.countByStatus("PAID");

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("paidOrders", paidOrders);

        log.info("Admin Dashboard loaded: users={}, products={}, pendingOrders={}",
                totalUsers, totalProducts, paidOrders);

        return "admin/dashboard";
    }

    @GetMapping("/manage-users")
    public String showManageUsers(Model model, @RequestParam(required = false) String email) {
        log.info("Called admin manage-users");
        if(email != null && !email.isEmpty()) {
            model.addAttribute("users", userService.findByEmail(email));
        } else {
            model.addAttribute("users", userService.findAll());
        }
        model.addAttribute("newUser", new User());
        model.addAttribute("searchEmail", email);
        return "admin/manage-users";
    }
    // Add User
    @PostMapping("/manage-users/add")
    public String addUser(@ModelAttribute("newUser") User user) {
        log.info("Called admin manage-users add user");
        userService.saveUser(user);
        return "redirect:/admin/manage-users";
    }

    @PostMapping("/manage-users/change")
    public String changeUserRole(@RequestParam Long id,
                                 @RequestParam String role) {
        log.info("Called admin manage-users change user role for user with id {}", id);
        userService.updateUserRole(id, role);
        return "redirect:/admin/manage-users";
    }

    @PostMapping("/manage-users/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/manage-users";
    }

    @GetMapping("/manage-products")
    public String showManageProducts(Model model,
                                     @RequestParam(required = false) String search,
                                     @RequestParam(required = false) Long id) {
        log.info("Called admin manage-products");

        // Filter products
        List<Product> products = (search != null && !search.isEmpty()) ?
                productService.findByNameOrCategory(search) :
                productService.getAllProducts();

        // selected product for update
        Product selectedProduct;
        if (id != null) {
            selectedProduct = productService.getProductById(id);
            if (selectedProduct == null) selectedProduct = new Product();
        } else if (search != null && !search.isEmpty() && !products.isEmpty()) {
            selectedProduct = products.get(0);
        } else {
            selectedProduct = new Product();
        }

        model.addAttribute("products", products);
        model.addAttribute("selectedProduct", selectedProduct);
        model.addAttribute("search", search);

        return "admin/manage-products";
    }

    // Add new product
    @PostMapping("/manage-products/add")
    public String addProduct(@ModelAttribute("newProduct") Product product) {
        log.info("Called admin manage-products add product");
        productService.addProduct(product);
        return "redirect:/admin/manage-products";
    }
    // Update product
    @PostMapping("/manage-products/update")
    public String updateProduct(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam String category,
                                @RequestParam double price,
                                @RequestParam int stock) {

        productService.updateProduct(id, name, description, category, price, stock);
        return "redirect:/admin/manage-products";
    }

    // Delete product
    @PostMapping("manage-products/delete")
    public String deleteProduct(@RequestParam("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/manage-products";
    }




    @GetMapping("/manage-orders")
    public String showManageOrders() {
        log.info("Called admin manage-orders");
        return "admin/manage-orders";
    }

    @GetMapping("/manage-feedback")
    public String showManageFeedback() {
        log.info("Called admin manage-feedback");
        return "admin/manage-feedback";
    }
}
