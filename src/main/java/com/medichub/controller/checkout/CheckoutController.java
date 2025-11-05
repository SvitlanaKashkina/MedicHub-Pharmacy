package com.medichub.controller.checkout;


import com.medichub.controller.cart.CartController;
import com.medichub.model.Cart;
import com.medichub.model.User;
import com.medichub.repository.CartRepository;
import com.medichub.repository.UserRepository;
import com.medichub.service.cart.CartService;
import com.medichub.service.order.OrderService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@AllArgsConstructor
@RequestMapping("/cart/")
public class CheckoutController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @GetMapping("/checkout")
    public String showCheckoutPage(Principal principal, Model model) {
        log.info("Called checkout page");
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Retrieve existing cart or create a new one
        Cart cart = cartService.getOrCreateCart(user);

        // Check if the shopping cart is empty
        if (cart.getCartItems().isEmpty()) {
            model.addAttribute("error", "Your shopping cart is empty. Please add products.");
            return "cart/cart"; // zurück zur Cart-Seite
        }

        model.addAttribute("cart", cart);
        return "checkout/checkout";
    }

    @PostMapping("/checkout/confirm")
    public String confirmOrder(@RequestParam String street,
                               @RequestParam String city,
                               @RequestParam String zip,
                               @RequestParam String country,
                               @RequestParam String cardNumber,
                               @RequestParam String expiryDate,
                               @RequestParam String cvv,
                               Principal principal, // currently logged-in user
                               Model model) {

        if (principal == null) {
            log.warn("User is not logged in");
            return "redirect:/login";
        }

        log.info("Called confirm order for user with email: {}", principal.getName());

        // Process order
        try {
            orderService.processOrder(
                    principal.getName(), street, city, zip, country, cardNumber, expiryDate, cvv
            );
            model.addAttribute("message", "Order completed successfully!");
            log.info("Order for user with email {} completed successfully", principal.getName());
            return "checkout/checkout-success";
        } catch (RuntimeException ex) {
            log.error("Error during order processing: {}", ex.getMessage());
            model.addAttribute("error", ex.getMessage());
            return "checkout/checkout";
        }
    }
}